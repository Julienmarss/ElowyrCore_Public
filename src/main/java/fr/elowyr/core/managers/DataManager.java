package fr.elowyr.core.managers;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.interfaces.ThrowableBiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {

    private static DataManager instance;
    private final Plugin plugin;
    private String url;
    private String username;
    private String password;
    private int validTimeout;
    private int loginTimeout;
    private Map<String, String> tables;
    private List<String> initialRequests;
    private Connection connection;
    
    public DataManager(final Plugin plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        final File file = new File(this.plugin.getDataFolder(), "mysql.yml");
        if (!file.exists()) {
            this.plugin.saveResource("mysql.yml", false);
        }
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.url = config.getString("url");
        this.username = config.getString("username");
        this.password = config.getString("password");
        this.loginTimeout = config.getInt("timeout.login", 5);
        this.validTimeout = config.getInt("timeout.valid", 10);
        this.tables = new HashMap<>();
        final ConfigurationSection tableSection = config.getConfigurationSection("tables");
        if (tableSection != null) {
            for (final String name : tableSection.getKeys(false)) {
                this.tables.put(name, tableSection.getString(name));
            }
        }
        final StringBuilder request = new StringBuilder();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.plugin.getResource("requests.sql")), StandardCharsets.UTF_8));
            reader.lines().forEach(request::append);
            reader.close();
        }
        catch (Throwable ignored) {}
        final String initialRequest = this.buildRequest(request.toString());
        this.initialRequests = Arrays.stream(initialRequest.split(";")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        ElowyrCore.info("Database loaded");
    }
    
    public boolean isConnected() {
        try {
            return this.connection != null && this.connection.isValid(this.validTimeout);
        }
        catch (SQLException ignored) {
            return false;
        }
    }
    
    public void disconnect() {
        try {
            if (this.isConnected()) {
                this.connection.close();
            }
        }
        catch (Throwable ignored) {}
    }
    
    public Connection getConnection() throws Exception {
        if (!this.isConnected()) {
            DriverManager.setLoginTimeout(this.loginTimeout);
            try {
                System.out.println("Connecting...");
                if (this.username != null) {
                    this.connection = DriverManager.getConnection(this.url, this.username, this.password);
                }
                else {
                    this.connection = DriverManager.getConnection(this.url);
                }
                System.out.println("Connected !");
            }
            catch (SQLException ex) {
                ElowyrCore.severe("Unable to connect to database");
                throw new Exception("Unable to connect", ex);
            }
            try {
                final Statement statement = this.connection.createStatement();
                for (final String request : this.initialRequests) {
                    statement.executeUpdate(request);
                }
                statement.close();
                System.out.println("Tables created !");
            }
            catch (SQLException ex) {
                throw new Exception("Unable to create tables", ex);
            }
        }
        return this.connection;
    }
    
    public void updateSync(final String request, final Object[] args, final ThrowableBiConsumer<Integer, Throwable> callback) {
        try {
            final PreparedStatement ps = this.prepareStatement(request);
            for (int i = 0; i < args.length; ++i) {
                ps.setObject(i + 1, args[i]);
            }
            final int value = ps.executeUpdate();
            if (callback != null) {
                callback.accept(value, null);
            }
            ps.close();
        }
        catch (Throwable ex) {
            try {
                if (callback != null) {
                    callback.accept(null, ex);
                }
            }
            catch (Throwable ignored) {}
        }
    }
    
    public void updateSyncWithGenKeys(final String request, final Object[] args, final ThrowableBiConsumer<ResultSet, Throwable> callback) {
        try {
            final PreparedStatement ps = this.prepareStatementWithGenKeys(request);
            for (int i = 0; i < args.length; ++i) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeUpdate();
            if (callback != null) {
                callback.accept(ps.getGeneratedKeys(), null);
            }
            ps.close();
        }
        catch (Throwable ex) {
            try {
                if (callback != null) {
                    callback.accept(null, ex);
                }
            }
            catch (Throwable ignored) {}
        }
    }
    
    public void bulkUpdateSync(final String request, final Object[][] args, final ThrowableBiConsumer<Integer, Throwable> callback) {
        try {
            final Connection con = this.getConnection();
            final PreparedStatement ps = con.prepareStatement(this.buildRequest(request));
            try {
                con.setAutoCommit(false);
                for (final Object[] array : args) {
                    for (int i = 0; i < array.length; ++i) {
                        ps.setObject(i + 1, array[i]);
                    }
                    ps.executeUpdate();
                }
                con.commit();
                con.setAutoCommit(true);
            }
            catch (Throwable t) {
                con.rollback();
                ps.close();
                throw t;
            }
        }
        catch (Throwable ex) {
            try {
                if (callback != null) {
                    callback.accept(null, ex);
                }
            }
            catch (Throwable ignored) {}
        }
    }
    
    public void querySync(final String request, final Object[] args, final ThrowableBiConsumer<ResultSet, Throwable> callback) {
        try {
            final PreparedStatement ps = this.prepareStatement(request);
            for (int i = 0; i < args.length; ++i) {
                ps.setObject(i + 1, args[i]);
            }
            final ResultSet rs = ps.executeQuery();
            if (callback != null) {
                callback.accept(rs, null);
            }
            rs.close();
            ps.close();
        }
        catch (Throwable ex) {
            try {
                if (callback != null) {
                    callback.accept(null, ex);
                }
            }
            catch (Throwable ignored) {}
        }
    }
    
    public void updateAsync(final String request, final Object[] args, final ThrowableBiConsumer<Integer, Throwable> callback) {
        this.runAsync(() -> this.updateSync(request, args, callback));
    }
    
    public void updateAsyncWithGenKeys(final String request, final Object[] args, final ThrowableBiConsumer<ResultSet, Throwable> callback) {
        this.runAsync(() -> this.updateSyncWithGenKeys(request, args, callback));
    }
    
    public void bulkUpdateAsync(final String request, final Object[][] args, final ThrowableBiConsumer<Integer, Throwable> callback) {
        this.runAsync(() -> this.bulkUpdateSync(request, args, callback));
    }
    
    public void queryAsync(final String request, final Object[] args, final ThrowableBiConsumer<ResultSet, Throwable> callback) {
        this.runAsync(() -> this.querySync(request, args, callback));
    }
    
    public void runAsync(final Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(ElowyrCore.getInstance(), task);
    }
    
    private String buildRequest(String request) {
        for (final Map.Entry<String, String> table : this.tables.entrySet()) {
            request = request.replace('{' + table.getKey() + '}', table.getValue());
        }
        return request;
    }

    public PreparedStatement prepareStatement(final String request) throws Throwable {
        return this.getConnection().prepareStatement(this.buildRequest(request));
    }
    
    private PreparedStatement prepareStatementWithGenKeys(final String request) throws Throwable {
        return this.getConnection().prepareStatement(this.buildRequest(request), 1);
    }

    public static void set(final DataManager instance) {
        DataManager.instance = instance;
    }
    
    public static DataManager get() {
        return DataManager.instance;
    }

    public static DataManager getInstance() {
        return instance;
    }

    public static void setInstance(DataManager instance) {
        DataManager.instance = instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getValidTimeout() {
        return validTimeout;
    }

    public void setValidTimeout(int validTimeout) {
        this.validTimeout = validTimeout;
    }

    public int getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public Map<String, String> getTables() {
        return tables;
    }

    public void setTables(Map<String, String> tables) {
        this.tables = tables;
    }

    public List<String> getInitialRequests() {
        return initialRequests;
    }

    public void setInitialRequests(List<String> initialRequests) {
        this.initialRequests = initialRequests;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

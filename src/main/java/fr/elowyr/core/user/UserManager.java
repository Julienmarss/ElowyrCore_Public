package fr.elowyr.core.user;

import fr.elowyr.core.user.data.User;
import fr.elowyr.core.utils.DbUtils;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class UserManager
{
    public static final Queue<User> cache = new ConcurrentLinkedQueue<>();
    private static final Queue<User> onlineUsers = new ConcurrentLinkedQueue<>();
    
    public static void getOrLoad(final UUID uniqueId, final Consumer<User> consumer) {
        final User user = getByUniqueId(uniqueId);
        if (user == null) {
            DbUtils.loadUser(uniqueId, usr -> {
                if (usr != null) {
                    UserManager.cache.add(usr);
                }
                consumer.accept(usr);
            });
        }
    }
    
    public static void getOrLoadByUsername(final String username, final Consumer<User> consumer) {
        final User user = getByUsername(username);
        if (user != null) {
            consumer.accept(user);
            return;
        }
        DbUtils.loadUserByUsername(username, usr -> {
            if (usr != null) {
                UserManager.cache.add(usr);
            }
            consumer.accept(usr);
        });
    }
    
    public static User get(final UUID uniqueId) {
        return UserManager.onlineUsers.stream().filter(u -> u.getUniqueId().equals(uniqueId)).findFirst().orElseGet(() -> getByUniqueId(uniqueId));
    }
    
    public static void addUser(final User user) {
        UserManager.cache.add(user);
    }
    
    public static void setOnline(final User user, final Player player, final boolean online) {
        if (online) {
            UserManager.onlineUsers.add(user);
        }
        else {
            UserManager.onlineUsers.remove(user);
        }
        user.setPlayer(player);
    }
    
    public static User getByUniqueId(final UUID uniqueId) {
        return UserManager.cache.stream().filter(user -> user.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }
    
    private static User getByUsername(final String username) {
        return UserManager.cache.stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public static Queue<User> getOnlineUsers() {
        return UserManager.onlineUsers;
    }
    
    public static void saveAll() {
        for (final User user : UserManager.onlineUsers) {
            user.save(false);
        }
    }
}

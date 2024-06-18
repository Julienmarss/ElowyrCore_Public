package fr.elowyr.core.utils;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.ap.data.AP;
import fr.elowyr.core.data.*;
import fr.elowyr.core.classement.data.ClassificationManager;
import fr.elowyr.core.classement.data.ClassificationParent;
import fr.elowyr.core.classement.data.FieldKey;
import fr.elowyr.core.managers.DataManager;
import fr.elowyr.core.user.data.User;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class DbUtils {
    public static void loadUser(final UUID uniqueId, final Consumer<User> consumer) {
        DataManager.get().querySync("SELECT * FROM `{users}` AS u LEFT JOIN `{boosts}` AS b ON b.user = u.id WHERE u.`unique_id` = ?", new Object[]{uniqueId.toString()}, (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else {
                User user = null;
                if (rs.next()) {
                    user = loadUser(rs);
                }
                consumer.accept(user);
            }
        });
    }

    public static void insertUser(final User user) {
        DataManager.get().updateAsyncWithGenKeys("INSERT INTO `{users}` (`unique_id`, `username`, `serial_kill`, `fly_time`, `boost_delay`, `votes`) VALUES (?, ?, ?, ?, ?, ?)", new Object[]{user.getUniqueId().toString(), user.getUsername(), user.getSerialKill(), user.getFlyTime(), user.getBoostdelay(), user.getVotes()}, (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        });
    }

    public static User getUserById(String uniqueId) throws Throwable {
        User user = null;
        ResultSet rs = DataManager.get().prepareStatement("SELECT * FROM `{users}`").executeQuery();
        while (rs.next()) {
            user = loadUser(rs);
        }
        return user;
    }

    public static void loadUserByUsername(final String username, final Consumer<User> consumer) {
        DataManager.get().querySync("SELECT * FROM `{users}` AS u LEFT JOIN `{boosts}` AS b ON b.user = u.id WHERE u.`username` = ?", new Object[]{username}, (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else {
                User user = null;
                if (rs.next()) {
                    user = loadUser(rs);
                }
                consumer.accept(user);
            }
        });
    }

    private static User loadUser(final ResultSet rs) throws Throwable {
        final long id = rs.getLong("u.id");
        final UUID uniqueId = UUID.fromString(rs.getString("u.unique_id"));
        final String username = rs.getString("u.username");
        final int serialKill = rs.getInt("u.serial_kill");
        final long boostdelay = rs.getLong("u.boost_delay");
        final int flyTime = rs.getInt("u.fly_time");
        final int votes = rs.getInt("u.votes");
        final User user = new User(id, uniqueId, username, serialKill, flyTime, boostdelay, votes);
        do {
            final Long boostId = (Long) rs.getObject("b.id");
            if (boostId == null) {
                break;
            }
            final BoostType type = BoostType.valueOf(rs.getString("b.type"));
            final int value = rs.getInt("b.value");
            final int time = rs.getInt("b.time");
            user.addBoost(new Boost(boostId, user.getId(), type, value, time));
        } while (rs.next());
        return user;
    }

    public static void updateUser(final User user, final Object... data) {
        if ((data.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        Bukkit.getScheduler().runTaskAsynchronously(ElowyrCore.getInstance(), () -> updateUserSync(user, data));
    }

    public static void updateUserSync(final User user, final Object... data) {
        if ((data.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        final StringBuilder sb = new StringBuilder("UPDATE `{users}` SET ");
        final List<Object> args = new LinkedList<Object>();
        for (int i = 0; i < data.length; i += 2) {
            if (!(data[i] instanceof String)) {
                throw new IllegalArgumentException("data[" + i + "] must be a column name");
            }
            if (i != 0) {
                sb.append(", ");
            }
            sb.append('`').append(data[i]).append("` = ?");
            args.add(data[i + 1]);
        }
        sb.append(" WHERE `id` = ?");
        args.add(user.getId());
        DataManager.get().updateSync(sb.toString(), args.toArray(), null);
    }

    public static void updateUsers(final List<User> users, final String[] keys, final Function<? super User, ?>[] values) {
        if (keys.length == 0) {
            return;
        }
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Key and Value length must be equal");
        }
        final StringBuilder sb = new StringBuilder("UPDATE `{users}` SET ");
        final Object[][] args = new Object[users.size()][];
        for (int i = 0; i < keys.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append('`').append(keys[i]).append("` = ?");
        }
        sb.append("WHERE `id` = ?");
        int index = 0;
        for (final User user : users) {
            final Object[] userData = new Object[keys.length + 1];
            for (int j = 0; j < values.length; ++j) {
                userData[j] = values[j].apply(user);
            }
            userData[values.length] = user.getId();
            args[index++] = userData;
        }
        DataManager.get().bulkUpdateAsync(sb.toString(), args, (_i, err) -> {
            if (err != null) {
                err.printStackTrace();
            }
        });
    }

    public static void insertBoost(final Boost boost) {
        DataManager.get().updateAsyncWithGenKeys("INSERT INTO `{boosts}` (`user`, `type`, `value`, `time`) VALUES (?, ?, ?, ?)", new Object[]{boost.getUserId(), boost.getType().name(), boost.getValue(), boost.getTime()}, (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else if (rs.next()) {
                boost.setId(rs.getLong(1));
            }
        });
    }

    public static void updateBoost(final Boost boost, final Object... data) {
        if ((data.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        final StringBuilder sb = new StringBuilder("UPDATE `{boosts}` SET ");
        final List<Object> args = new LinkedList<Object>();
        for (int i = 0; i < data.length; i += 2) {
            if (!(data[i] instanceof String)) {
                throw new IllegalArgumentException("data[" + i + "] must be a column name");
            }
            if (i != 0) {
                sb.append(", ");
            }
            sb.append('`').append(data[i]).append("` = ?");
            args.add(data[i + 1]);
        }
        sb.append(" WHERE `id` = ?");
        args.add(boost.getId());
        DataManager.get().updateAsync(sb.toString(), args.toArray(), null);
    }

    public static void deleteUserFinishedBoosts(final List<Boost> boosts) {
        final List<Object> args = new LinkedList<>();
        final StringBuilder sb = new StringBuilder("DELETE FROM `{boosts}` WHERE `id` IN (");
        boolean first = true;
        for (final Boost boost : boosts) {
            if (!first) {
                sb.append(", ");
            }
            sb.append('?');
            args.add(boost.getId());
            first = false;
        }
        sb.append(')');
        DataManager.get().updateAsync(sb.toString(), args.toArray(), (_i, err) -> {
            if (err != null) {
                err.printStackTrace();
            }
        });
    }

    public static <U> void loadClassification(final ClassificationManager<U> manager, final Function<String, U> uuidCreator) {
        final FieldKey[] keys = manager.getKeys();
        DataManager.get().querySync("SELECT * FROM `{" + manager.getTableName() + "}`", new Object[0], (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    Object uuid = uuidCreator.apply(rs.getString("uuid"));
                    String display = rs.getString("display");
                    double[] fields = new double[keys.length];
                    for (FieldKey key : keys)
                        fields[key.getId()] = rs.getDouble(key.getName());
                    manager.addLast(new ClassificationParent(id, uuid, display, fields));
                }
            }
        });
    }

    public static <U> void insertClassification(final ClassificationManager<U> manager, final ClassificationParent<U> parent) {
        final FieldKey[] keys = manager.getKeys();
        final StringBuilder sb = new StringBuilder("INSERT INTO `{" + manager.getTableName() + "}` (`uuid`, `display`");
        final List<Object> args = new LinkedList<>();
        args.add(parent.getUuid().toString());
        args.add(parent.getDisplay());
        for (final FieldKey key : keys) {
            sb.append(", `").append(key.getName()).append('`');
            args.add(parent.get(key));
        }
        sb.append(") VALUES (?, ?");
        for (int i = 0; i < keys.length; ++i) {
            sb.append(", ?");
        }
        sb.append(')');
        DataManager.get().updateAsyncWithGenKeys(sb.toString(), args.toArray(), (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else if (rs.next()) {
                parent.setId(rs.getLong(1));
            }
        });
    }

    public static <U> void updateClassificationDisplay(final ClassificationManager<U> manager, final ClassificationParent<U> parent) {
        DataManager.get().updateAsync("UPDATE `{" + manager.getTableName() + "}` SET `display` = ? WHERE `id` = ?", new Object[]{parent.getDisplay(), parent.getId()}, null);
    }

    public static <U> void updateClassification(ClassificationManager<U> manager, Consumer<Integer> callback) {
        Set<ClassificationParent<U>> changes = manager.getChanges();
        if (changes.isEmpty()) {
            callback.accept(0);
            return;
        }
        StringBuilder sb = new StringBuilder("UPDATE `{" + manager.getTableName() + "}` SET ");
        FieldKey[] keys = manager.getKeys();
        int changeCount = changes.size();
        boolean first = true;
        for (FieldKey key : keys) {
            if (!first)
                sb.append(", ");
            sb.append('`').append(key.getName()).append("` = ?");
            first = false;
        }
        sb.append(" WHERE `id` = ?");
        Object[][] args = changes.stream().map(parent -> {
            Object[] data = new Object[keys.length + 1];
            for (int i = 0; i < keys.length; i++)
                data[i] = parent.get(keys[i]);
            data[keys.length] = parent.getId();
            return data;
        }).toArray(Object[][]::new);
        DataManager.get().bulkUpdateSync(sb.toString(), args, (_l, err) -> {
            if (err != null) {
                err.printStackTrace();
                return;
            }
            if (callback != null)
                callback.accept(changeCount);
        });
    }

    public static <U> void deleteClassification(final ClassificationManager<U> manager, final ClassificationParent<U> parent) {
        DataManager.get().updateAsync("DELETE FROM `{" + manager.getTableName() + "}` WHERE `id` = ?", new Object[]{parent.getId()}, null);
    }

    public static <U> void deleteClassifications(final ClassificationManager<U> manager) {
        DataManager.get().updateAsync("DELETE FROM `{" + manager.getTableName() + "}`", new Object[0], null);
    }

    public static <U> void resetClassification(final ClassificationManager<U> manager, final FieldKey key) {
        DataManager.get().updateAsync("UPDATE `{" + manager.getTableName() + "}` SET `" + key.getName() + "` = 0", new Object[0], null);
    }

    public static void deleteAp(final AP ap) {
        DataManager.get().updateAsync("DELETE FROM `{aps}` WHERE `id` = ?", new Object[]{ap.getId()}, null);
    }

    public static void updateAp(final AP ap, final Object... data) {
        if ((data.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        Bukkit.getScheduler().runTaskAsynchronously(ElowyrCore.getInstance(), () -> updateApSync(ap, data));
    }

    public static void updateApSync(final AP ap, final Object... data) {
        if ((data.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        final StringBuilder sb = new StringBuilder("UPDATE `{aps}` SET ");
        final List<Object> args = new LinkedList<Object>();
        for (int i = 0; i < data.length; i += 2) {
            if (!(data[i] instanceof String)) {
                throw new IllegalArgumentException("data[" + i + "] must be a column name");
            }
            if (i != 0) {
                sb.append(", ");
            }
            sb.append('`').append(data[i]).append("` = ?");
            args.add(data[i + 1]);
        }
        sb.append(" WHERE `id` = ?");
        args.add(ap.getId());
        DataManager.get().updateSync(sb.toString(), args.toArray(), null);
    }

    public static AP loadAp(final ResultSet rs) throws Throwable {
        final long id = rs.getLong("id");
        final int x = rs.getInt("x");
        final int z = rs.getInt("z");
        final String world = rs.getString("world");
        final long price = rs.getLong("price");
        final boolean isVIP = rs.getBoolean("vip");
        final String currentOwnerId = rs.getString("owner-id");
        final long buyAt = rs.getLong("date");
        final String chunkAsString = rs.getString("chunk");
        final AP ap = new AP(id, x, z, world, price, isVIP, currentOwnerId, buyAt, chunkAsString);
        return ap;
    }

    public static void insertAp(final AP ap) {
        DataManager.get().updateAsyncWithGenKeys("INSERT INTO `{aps}` (`id`, `x`, `z`, `world`, `price`, `vip`, `owner-id`, `date`, `chunk`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ap.getId(), ap.getX(), ap.getZ(), ap.getWorld(), ap.getPrice(), ap.isVIP(), ap.getCurrentOwnerId(), ap.getBuyAt(), ap.getChunkAsString()}, (rs, err) -> {
            if (err != null) {
                err.printStackTrace();
            } else if (rs.next()) {
                ap.setId(rs.getLong(1));
            }
        });
    }


}

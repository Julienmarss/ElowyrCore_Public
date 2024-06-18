package fr.elowyr.core.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownUtils {

	private static HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap();

	public static void createCooldown(String alias) {
		if (cooldowns.containsKey(alias)) {
			return;
		}
		cooldowns.put(alias, new HashMap());
	}

	public static void addCooldown(String alias, Player player, int seconds) {
		if (!cooldowns.containsKey(alias)) {
			CooldownUtils.createCooldown(alias);
		}
		long next = System.currentTimeMillis() + (long)seconds * 1000;
		cooldowns.get(alias).put(player.getUniqueId(), next);
	}

	public static void removeCooldown(String alias, Player player) {
		if (!cooldowns.containsKey(alias)) {
			return;
		}
		cooldowns.get(alias).remove(player.getUniqueId());
	}

	public static HashMap<UUID, Long> getCooldownMap(String alias) {
		if (cooldowns.containsKey(alias)) {
			return cooldowns.get(alias);
		}
		return null;
	}

	public static boolean isOnCooldown(String alias, Player player) {
		if (cooldowns.containsKey(alias) && cooldowns.get(alias).containsKey(player.getUniqueId()) && System.currentTimeMillis() <= cooldowns.get(alias).get(player.getUniqueId())) {
			return true;
		}
		return false;
	}

	public static int getCooldownForPlayerInt(String alias, Player player) {
		return (int)CooldownUtils.getCooldownForPlayerLong(alias, player);
	}

	public static long getCooldownForPlayerLong(String alias, Player player) {
		return cooldowns.get(alias).get(player.getUniqueId()) - System.currentTimeMillis();
	}

	public static void clearCooldowns() {
		cooldowns.clear();
	}
}


package fr.elowyr.core.utils;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultUtils {
	 
	private static Economy economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	private static Chat chat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
	
    public static double getBalance(String user) {
        return economy.getBalance(user);
    }
   
    public static double getBalance(Player player) {
        return economy.getBalance(player);
    }
   
    public static void setBalance(Player player, double value) {
        economy.withdrawPlayer(player, value);
        economy.depositPlayer(player, value);
    }
   
    public static boolean has(Player player, double value) {
    	return economy.has(player, value);
    }
    
    public static void depositMoney(Player player, double value) {
        economy.depositPlayer(player, value);
    }
   
    public static void depositMoney(String player, double value) {
        economy.depositPlayer(player, value);
    }
   
    public static void withdrawMoney(Player player, double value) {
        economy.withdrawPlayer(player, value);
    }
    
    public static String getGroup(Player player) {
    	return chat.getPrimaryGroup(player);
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static void setEconomy(Economy economy) {
        VaultUtils.economy = economy;
    }

    public static Chat getChat() {
        return chat;
    }

    public static void setChat(Chat chat) {
        VaultUtils.chat = chat;
    }
}
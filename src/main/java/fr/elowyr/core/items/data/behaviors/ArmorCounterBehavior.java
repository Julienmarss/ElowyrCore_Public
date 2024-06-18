package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ArmorCounterBehavior extends BasicBehavior {

    private int maxUses;

    public ArmorCounterBehavior() {
        super(UseType.INTERACT_PLAYER);
    }

    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.maxUses = section.getInt("maxUses", 5);
    }

    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("uses", 0);
    }

    @Override
    public boolean use(ItemContext<?> context) {
        final PlayerInteractEntityEvent event = (PlayerInteractEntityEvent) context.getEvent();
        Player player = event.getPlayer();

        Player target = (Player) event.getRightClicked();

        if (!(event.getRightClicked() instanceof Player)) return false;

        if (target.getInventory().getHelmet() == null && target.getInventory().getChestplate() == null && target.getInventory().getLeggings() == null && target.getInventory().getBoots() == null) {
            player.sendMessage(Utils.color("&a&lDurabilité &7◆ &6&l" + target.getName() + "&f n'a pas d'armure."));
            return false;
        }

        if (context.getData().addInt("uses", 1) >= this.maxUses) {
            context.useItem(1);
            context.getData().setInt("uses", 0);
            context.getPlayer().playSound(context.getPlayer().getLocation(), Sound.ITEM_BREAK, 5, 5);
        }

        player.sendMessage(Utils.color("&a&lDurabilité &7◆ &6&l" + target.getName()));
        if (target.getInventory().getHelmet() != null) {
            //POUR METTRE EN % (MaxDura - duraActu) / MaxDura * 100
            player.sendMessage(Utils.color("&e" + (target.getInventory().getHelmet().getType().getMaxDurability() - target.getInventory().getHelmet().getDurability()) + " &fdurabilités sur son &7Casque&f."));
        } else {
            player.sendMessage(Utils.color("&e" + "0 &fdurabilités sur son &7Casque&f."));
        }
        if (target.getInventory().getChestplate() != null ) {
            player.sendMessage(Utils.color("&e" + (target.getInventory().getChestplate().getType().getMaxDurability() - target.getInventory().getChestplate().getDurability()) + " &fdurabilités sur son &7Plastron&f."));
        } else {
            player.sendMessage(Utils.color("&e" + "0 &fdurabilités sur son &7Plastron&f."));
        }
        if (target.getInventory().getLeggings() != null) {
            player.sendMessage(Utils.color("&e" + (target.getInventory().getLeggings().getType().getMaxDurability() - target.getInventory().getLeggings().getDurability()) + " &fdurabilités sur son &7Pantalon&f."));
        } else {
            player.sendMessage(Utils.color("&e" + "0 &fdurabilités sur son &7Pantalon&f."));
        }
        if (target.getInventory().getBoots() != null) {
            player.sendMessage(Utils.color("&e" + (target.getInventory().getBoots().getType().getMaxDurability() - target.getInventory().getBoots().getDurability()) + " &fdurabilités sur ses &7Bottes&f."));
        } else {
            player.sendMessage(Utils.color("&e" +"0 &fdurabilités sur ses &7Bottes&f."));
        }

        return true;
    }
}

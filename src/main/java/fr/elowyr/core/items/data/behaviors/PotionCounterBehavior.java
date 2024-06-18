package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Arrays;
import java.util.Objects;

public class PotionCounterBehavior extends BasicBehavior {

    private int maxUses;

    public PotionCounterBehavior() {
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

        if (context.getData().addInt("uses", 1) >= this.maxUses) {
            context.useItem(1);
            context.getData().setInt("uses", 0);
            context.getPlayer().playSound(context.getPlayer().getLocation(), Sound.ITEM_BREAK, 5, 5);
        }
        player.sendMessage(Utils.color("&dPotions &7◆ &6&l" + target.getName() + " &fpossède &5" + getPotions(target) + " potion(s)&f."));
        return true;
    }

    public static long getPotions(Player player) {
        if (player == null || !player.isOnline()) {
            return 0;
        }
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType() == Material.POTION)
                .filter(item -> item.getDurability() == 16421).count();
    }
}

package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GodDagerBehavior extends BasicBehavior {

    private String defaultLastKill;

    public GodDagerBehavior() {
        super(UseType.PLAYER_KILL);
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        this.defaultLastKill = Utils.color(section.getString("default-last-kill", "&cAucun"));
    }
    
    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("kill", 0);
        data.setString("last_kill", this.defaultLastKill);
    }
    
    @Override
    public boolean use(final ItemContext<?> ctx) {
        final Player player = ((PlayerDeathEvent)ctx.getEvent()).getEntity();
        ctx.getData().setString("last_kill", player.getName());
        ctx.getData().addInt("kill", 1);
        ctx.getPlayer().getInventory().addItem(ItemBuilder.newBuilder(Material.SKULL_ITEM, 3).owner(player.getName()).build());
        ctx.getPlayer().updateInventory();
        return false;
    }
}

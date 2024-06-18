package fr.elowyr.core.tasks;

import com.massivecraft.factions.*;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.RegionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceTask extends BukkitRunnable
{
    private final Player placer;
    private final FPlayer fPlacer;
    private final Material type;
    private final byte data;
    private final BlockFace face;
    private Block current;
    private int placedBlock = 0;
    
    public PlaceTask(final Player placer, final Material type, final byte data, final BlockFace face, final Block start) {
        this.placer = placer;
        this.type = type;
        this.data = data;
        this.face = face;
        this.current = start;
        this.fPlacer = FPlayers.getInstance().getByPlayer(placer);
    }
    
    public void run() {
        if (!BukkitUtils.canPlace(this.current, this.placer, this.fPlacer)) {
            this.cancel();
            return;
        }
        if (face != null) {
            if (face != BlockFace.UP && placedBlock >= 32) {
                this.cancel();
                return;
            }
        }

        if (this.current.getType() != Material.AIR && this.current.getType() != Material.LAVA && this.current.getType() != Material.WATER ||
                RegionUtils.inArea(this.current.getLocation(), this.current.getWorld(), "warzone") ||
                RegionUtils.inArea(this.current.getLocation(), this.current.getWorld(), "spawn")) {
            this.cancel();
            return;
        }

        this.current.setType(this.type);
        this.current.setData(this.data);
        this.current = this.current.getRelative(this.face);
        placedBlock++;
    }
}

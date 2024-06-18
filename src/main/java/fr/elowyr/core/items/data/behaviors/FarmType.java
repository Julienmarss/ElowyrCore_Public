package fr.elowyr.core.items.data.behaviors;

import org.bukkit.Material;

public enum FarmType {

     CARROTS(Material.CARROT, Material.CARROT_ITEM, 7),
     WHEAT(Material.CROPS, Material.WHEAT, 7),
     POTATOES(Material.POTATO, Material.POTATO_ITEM, 7),
     NETHER_WARTS(Material.NETHER_WARTS, Material.NETHER_STALK, 3),
     ;

     private final Material material;
     private final Material drops;
     private final int data;

     FarmType(Material material, Material drops, int data) {
          this.material = material;
          this.drops = drops;
          this.data = data;
     }

     public Material getMaterial() {
          return material;
     }

     public Material getDrops() {
          return drops;
     }

     public int getData() {
          return data;
     }
}

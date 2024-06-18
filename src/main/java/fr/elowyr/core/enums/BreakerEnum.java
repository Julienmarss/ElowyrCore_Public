package fr.elowyr.core.enums;

import org.bukkit.Material;

import java.util.Arrays;


public enum BreakerEnum {
    OBSIDIAN(Material.OBSIDIAN, 12),
    ;

    private Material material;
    private int number;

    BreakerEnum(Material material, int number) {
        this.material = material;
        this.number = number;
    }

    public static int getByMaterial(Material material) {
        if (Arrays.stream(values()).anyMatch(breakerEnum -> breakerEnum.getMaterial() == material)) {
            return Arrays.stream(values()).filter(breakerEnum -> breakerEnum.getMaterial() == material).findFirst().get().getNumber();
        }
        return 1;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

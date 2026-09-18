package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;
import org.bukkit.Material;

public final class WeaponProfileResolver {

    public WeaponProfile resolve(Material material) {
        Objects.requireNonNull(material, "material");
        String name = material.name();

        if (name.endsWith("_SWORD")) {
            return new WeaponProfile(tierDamage(name, 4.0, 4.0, 5.0, 6.0, 7.0, 8.0), AttackSpeed.NORMAL);
        }
        if (name.endsWith("_AXE")) {
            return new WeaponProfile(tierDamage(name, 5.0, 7.0, 9.0, 9.0, 9.0, 10.0), AttackSpeed.SLOW);
        }
        if (name.equals("MACE")) {
            return new WeaponProfile(10.0, AttackSpeed.VERY_SLOW);
        }
        if (name.equals("TRIDENT")) {
            return new WeaponProfile(8.0, AttackSpeed.NORMAL);
        }

        return new WeaponProfile(2.0, AttackSpeed.FAST);
    }

    private static double tierDamage(
            String materialName,
            double wooden,
            double golden,
            double stone,
            double iron,
            double diamond,
            double netherite) {
        if (materialName.startsWith("WOODEN_")) {
            return wooden;
        }
        if (materialName.startsWith("GOLDEN_")) {
            return golden;
        }
        if (materialName.startsWith("STONE_")) {
            return stone;
        }
        if (materialName.startsWith("IRON_")) {
            return iron;
        }
        if (materialName.startsWith("DIAMOND_")) {
            return diamond;
        }
        if (materialName.startsWith("NETHERITE_")) {
            return netherite;
        }
        return wooden;
    }
}

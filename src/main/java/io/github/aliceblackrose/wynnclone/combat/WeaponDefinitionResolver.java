package io.github.aliceblackrose.wynnclone.combat;

import java.util.Locale;
import java.util.Objects;
import org.bukkit.Material;

public final class WeaponDefinitionResolver {

    public WeaponDefinition resolve(Material material) {
        Objects.requireNonNull(material, "material");

        String name = material.name();
        String id = "minecraft:" + name.toLowerCase(Locale.ROOT);

        if (name.endsWith("_SWORD")) {
            return new WeaponDefinition(
                    id,
                    tierDamage(name, 4.0, 4.0, 5.0, 6.0, 7.0, 8.0),
                    AttackSpeed.NORMAL,
                    0.02);
        }
        if (name.endsWith("_AXE")) {
            return new WeaponDefinition(
                    id,
                    tierDamage(name, 5.0, 7.0, 9.0, 9.0, 9.0, 10.0),
                    AttackSpeed.SLOW,
                    0.0);
        }
        if (name.equals("MACE")) {
            return new WeaponDefinition(id, 10.0, AttackSpeed.VERY_SLOW, 0.05);
        }
        if (name.equals("TRIDENT")) {
            return new WeaponDefinition(id, 8.0, AttackSpeed.NORMAL, 0.03);
        }

        return new WeaponDefinition(id, 2.0, AttackSpeed.FAST, 0.0);
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

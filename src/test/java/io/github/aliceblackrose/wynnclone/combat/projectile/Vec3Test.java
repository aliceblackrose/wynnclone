package io.github.aliceblackrose.wynnclone.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class Vec3Test {
    @Test
    void normalizesAndAdvancesPredictably() {
        Vec3 direction = new Vec3(3.0, 0.0, 4.0).normalize();
        Vec3 end = new Vec3(1.0, 2.0, 3.0).add(direction.multiply(5.0));

        assertEquals(4.0, end.x(), 1.0e-9);
        assertEquals(2.0, end.y(), 1.0e-9);
        assertEquals(7.0, end.z(), 1.0e-9);
        assertEquals(1.0, direction.length(), 1.0e-9);
    }

    @Test
    void rejectsInvalidMathInputs() {
        assertThrows(IllegalArgumentException.class, () -> new Vec3(Double.NaN, 0.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new Vec3(1.0, 2.0, 3.0).multiply(Double.POSITIVE_INFINITY));
        assertThrows(IllegalStateException.class, () -> new Vec3(0.0, 0.0, 0.0).normalize());
    }
}

package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ManaPoolTest {
    @Test
    void spendAndRestoreRemainWithinBounds() {
        var mana = new ManaPool(100.0);
        assertTrue(mana.spend(30.0));
        assertEquals(70.0, mana.current());
        mana.restore(50.0);
        assertEquals(100.0, mana.current());
    }

    @Test
    void insufficientSpendDoesNotMutateMana() {
        var mana = new ManaPool(20.0);
        assertFalse(mana.spend(25.0));
        assertEquals(20.0, mana.current());
    }

    @Test
    void setCurrentClampsToPoolBounds() {
        var mana = new ManaPool(100.0);
        mana.setCurrent(-50.0);
        assertEquals(0.0, mana.current());
        mana.setCurrent(150.0);
        assertEquals(100.0, mana.current());
    }

    @Test
    void rejectsInvalidNumbers() {
        var mana = new ManaPool(100.0);
        assertThrows(IllegalArgumentException.class, () -> mana.spend(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> mana.restore(-1.0));
    }
}

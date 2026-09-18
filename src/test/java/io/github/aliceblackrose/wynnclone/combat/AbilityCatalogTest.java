package io.github.aliceblackrose.wynnclone.combat;

import static io.github.aliceblackrose.wynnclone.combat.CombatInput.LEFT;
import static io.github.aliceblackrose.wynnclone.combat.CombatInput.RIGHT;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class AbilityCatalogTest {
    @Test
    void rejectsDuplicateIds() {
        var catalog = new AbilityCatalog();
        catalog.register(definition("test.same", InputCombo.of(RIGHT, LEFT, LEFT)));

        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.register(definition("test.same", InputCombo.of(RIGHT, RIGHT, LEFT))));
    }

    @Test
    void rejectsDuplicateCombos() {
        var catalog = new AbilityCatalog();
        InputCombo combo = InputCombo.of(RIGHT, LEFT, LEFT);
        catalog.register(definition("test.first", combo));

        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.register(definition("test.second", combo)));
    }

    private static AbilityDefinition definition(String id, InputCombo combo) {
        return new AbilityDefinition(new AbilityId(id), id, combo, 0.0, 0L);
    }
}

package io.github.aliceblackrose.wynnclone.combat;

import static io.github.aliceblackrose.wynnclone.combat.CombatInput.LEFT;
import static io.github.aliceblackrose.wynnclone.combat.CombatInput.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ComboServiceTest {
    private static final AbilityId BASH = new AbilityId("test.bash");

    private ComboService service;
    private ComboBuffer buffer;

    @BeforeEach
    void setUp() {
        var catalog = new AbilityCatalog();
        catalog.register(new AbilityDefinition(BASH, "Bash", InputCombo.of(RIGHT, LEFT, LEFT), 10.0, 1000L));
        service = new ComboService(catalog, 1200L, 4);
        buffer = new ComboBuffer();
    }

    @Test
    void progressesThenResolvesExactCombo() {
        assertInstanceOf(ComboOutcome.Progress.class, service.accept(buffer, RIGHT, 100L));
        assertInstanceOf(ComboOutcome.Progress.class, service.accept(buffer, LEFT, 200L));

        ComboOutcome.Cast cast = assertInstanceOf(
                ComboOutcome.Cast.class,
                service.accept(buffer, LEFT, 300L));

        assertEquals(BASH, cast.abilityId());
        assertTrue(buffer.inputs().isEmpty());
    }

    @Test
    void invalidSequenceClearsBuffer() {
        service.accept(buffer, RIGHT, 100L);
        ComboOutcome outcome = service.accept(buffer, RIGHT, 200L);

        assertInstanceOf(ComboOutcome.Rejected.class, outcome);
        assertTrue(buffer.inputs().isEmpty());
    }

    @Test
    void expiredSequenceStartsFreshCombo() {
        service.accept(buffer, RIGHT, 100L);
        service.accept(buffer, LEFT, 200L);

        ComboOutcome outcome = service.accept(buffer, RIGHT, 1501L);

        ComboOutcome.Progress progress = assertInstanceOf(ComboOutcome.Progress.class, outcome);
        assertEquals(java.util.List.of(RIGHT), progress.inputs());
    }

    @Test
    void maximumLengthRejectsOversizedPrefix() {
        var catalog = new AbilityCatalog();
        catalog.register(new AbilityDefinition(
                new AbilityId("test.long"),
                "Long",
                InputCombo.of(RIGHT, RIGHT, RIGHT, RIGHT),
                0.0,
                0L));
        var shortService = new ComboService(catalog, 1200L, 2);
        var shortBuffer = new ComboBuffer();

        shortService.accept(shortBuffer, RIGHT, 0L);
        shortService.accept(shortBuffer, RIGHT, 10L);
        ComboOutcome outcome = shortService.accept(shortBuffer, RIGHT, 20L);

        assertInstanceOf(ComboOutcome.Rejected.class, outcome);
        assertTrue(shortBuffer.inputs().isEmpty());
    }
}

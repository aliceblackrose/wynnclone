package io.github.aliceblackrose.wynnclone.combat;

import java.util.List;
import java.util.Objects;

public sealed interface ComboOutcome {

    record Progress(List<CombatInput> inputs) implements ComboOutcome {
        public Progress {
            Objects.requireNonNull(inputs, "inputs");
            inputs = List.copyOf(inputs);
        }
    }

    record Cast(AbilityId abilityId) implements ComboOutcome {
        public Cast {
            Objects.requireNonNull(abilityId, "abilityId");
        }
    }

    record Rejected() implements ComboOutcome {
    }
}

package io.github.aliceblackrose.wynnclone.combat;

import java.util.List;
import java.util.Objects;

public record InputCombo(List<CombatInput> inputs) {

    public InputCombo {
        Objects.requireNonNull(inputs, "inputs");
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("A combo must contain at least one input");
        }
        if (inputs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Combo inputs must not contain null");
        }
        inputs = List.copyOf(inputs);
    }

    public static InputCombo of(CombatInput... inputs) {
        return new InputCombo(List.of(inputs));
    }

    public boolean startsWith(List<CombatInput> prefix) {
        Objects.requireNonNull(prefix, "prefix");
        if (prefix.size() > inputs.size()) {
            return false;
        }

        for (int index = 0; index < prefix.size(); index++) {
            if (inputs.get(index) != prefix.get(index)) {
                return false;
            }
        }
        return true;
    }
}

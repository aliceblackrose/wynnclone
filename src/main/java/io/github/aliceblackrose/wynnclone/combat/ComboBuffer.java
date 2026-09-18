package io.github.aliceblackrose.wynnclone.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ComboBuffer {

    private final List<CombatInput> inputs = new ArrayList<>();
    private long lastInputMillis;

    public boolean active() {
        return !inputs.isEmpty();
    }

    public List<CombatInput> inputs() {
        return List.copyOf(inputs);
    }

    public void append(CombatInput input, long nowMillis) {
        inputs.add(Objects.requireNonNull(input, "input"));
        lastInputMillis = nowMillis;
    }

    public boolean isExpired(long nowMillis, long timeoutMillis) {
        if (!active()) {
            return false;
        }
        if (timeoutMillis < 0L) {
            throw new IllegalArgumentException("Timeout must be non-negative");
        }
        return nowMillis < lastInputMillis || nowMillis - lastInputMillis > timeoutMillis;
    }

    public void clear() {
        inputs.clear();
        lastInputMillis = 0L;
    }
}

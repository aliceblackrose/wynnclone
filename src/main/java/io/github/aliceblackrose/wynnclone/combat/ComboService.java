package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public final class ComboService {
    private final AbilityCatalog catalog;
    private final long timeoutMillis;
    private final int maxLength;

    public ComboService(AbilityCatalog catalog, long timeoutMillis, int maxLength) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        if (timeoutMillis < 0L) {
            throw new IllegalArgumentException("Timeout must be non-negative");
        }
        if (maxLength < 1) {
            throw new IllegalArgumentException("Maximum combo length must be positive");
        }
        this.timeoutMillis = timeoutMillis;
        this.maxLength = maxLength;
    }

    public boolean active(ComboBuffer buffer, long nowMillis) {
        Objects.requireNonNull(buffer, "buffer");
        if (buffer.isExpired(nowMillis, timeoutMillis)) {
            buffer.clear();
        }
        return buffer.active();
    }

    public ComboOutcome accept(ComboBuffer buffer, CombatInput input, long nowMillis) {
        Objects.requireNonNull(buffer, "buffer");
        Objects.requireNonNull(input, "input");

        if (buffer.isExpired(nowMillis, timeoutMillis)) {
            buffer.clear();
        }

        buffer.append(input, nowMillis);
        if (buffer.inputs().size() > maxLength) {
            buffer.clear();
            return new ComboOutcome.Rejected();
        }

        var exact = catalog.findExact(buffer.inputs());
        if (exact.isPresent()) {
            var abilityId = exact.orElseThrow().id();
            buffer.clear();
            return new ComboOutcome.Cast(abilityId);
        }

        if (catalog.hasPrefix(buffer.inputs())) {
            return new ComboOutcome.Progress(buffer.inputs());
        }

        buffer.clear();
        return new ComboOutcome.Rejected();
    }
}

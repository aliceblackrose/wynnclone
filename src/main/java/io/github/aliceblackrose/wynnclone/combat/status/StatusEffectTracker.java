package io.github.aliceblackrose.wynnclone.combat.status;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class StatusEffectTracker {
    private final Map<StatusEffectType, ActiveStatusEffect> effects = new EnumMap<>(StatusEffectType.class);

    public void apply(UUID sourceId, StatusEffectSpec spec, long nowMillis) {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(spec, "spec");

        long expiresAt = safeAdd(nowMillis, spec.durationMillis());
        long nextTickAt = safeAdd(nowMillis, spec.tickIntervalMillis());
        ActiveStatusEffect incoming = new ActiveStatusEffect(
                spec.type(),
                sourceId,
                spec.strength(),
                expiresAt,
                nextTickAt,
                spec.tickIntervalMillis());

        effects.compute(spec.type(), (ignored, existing) -> merge(existing, incoming, spec.stackPolicy()));
    }

    public List<StatusEffectTick> pollDueTicks(long nowMillis) {
        List<StatusEffectTick> due = new ArrayList<>();
        var iterator = effects.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            ActiveStatusEffect effect = entry.getValue();

            if (nowMillis > effect.expiresAtMillis()) {
                iterator.remove();
                continue;
            }

            if (nowMillis >= effect.nextTickAtMillis()) {
                due.add(new StatusEffectTick(effect.type(), effect.sourceId(), effect.strength()));

                if (nowMillis >= effect.expiresAtMillis()) {
                    iterator.remove();
                } else {
                    long nextTick = safeAdd(nowMillis, effect.tickIntervalMillis());
                    entry.setValue(effect.withNextTick(nextTick));
                }
            }
        }

        return List.copyOf(due);
    }

    public Optional<ActiveStatusEffect> effect(StatusEffectType type) {
        return Optional.ofNullable(effects.get(Objects.requireNonNull(type, "type")));
    }

    public Set<StatusEffectType> activeTypes() {
        return Set.copyOf(effects.keySet());
    }

    public boolean isEmpty() {
        return effects.isEmpty();
    }

    private static ActiveStatusEffect merge(
            ActiveStatusEffect existing,
            ActiveStatusEffect incoming,
            StatusStackPolicy policy) {
        if (existing == null || policy == StatusStackPolicy.REPLACE) {
            return incoming;
        }

        return switch (policy) {
            case REPLACE -> incoming;
            case REFRESH -> new ActiveStatusEffect(
                    incoming.type(),
                    incoming.sourceId(),
                    Math.max(existing.strength(), incoming.strength()),
                    incoming.expiresAtMillis(),
                    Math.min(existing.nextTickAtMillis(), incoming.nextTickAtMillis()),
                    Math.min(existing.tickIntervalMillis(), incoming.tickIntervalMillis()));
            case STACK_INTENSITY -> new ActiveStatusEffect(
                    incoming.type(),
                    incoming.sourceId(),
                    safeStrengthAdd(existing.strength(), incoming.strength()),
                    Math.max(existing.expiresAtMillis(), incoming.expiresAtMillis()),
                    Math.min(existing.nextTickAtMillis(), incoming.nextTickAtMillis()),
                    Math.min(existing.tickIntervalMillis(), incoming.tickIntervalMillis()));
        };
    }

    private static double safeStrengthAdd(double left, double right) {
        double value = left + right;
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Stacked status effect strength is not finite");
        }
        return value;
    }

    private static long safeAdd(long value, long delta) {
        try {
            return Math.addExact(value, delta);
        } catch (ArithmeticException exception) {
            return Long.MAX_VALUE;
        }
    }
}

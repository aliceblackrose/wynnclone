package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record AbilityId(String value) {

    public AbilityId {
        Objects.requireNonNull(value, "value");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Ability id must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

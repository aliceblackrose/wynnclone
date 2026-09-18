package io.github.aliceblackrose.wynnclone.combat;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AbilityCatalog {

    private final Map<AbilityId, AbilityDefinition> byId = new LinkedHashMap<>();
    private final Map<InputCombo, AbilityDefinition> byCombo = new LinkedHashMap<>();

    public void register(AbilityDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        if (byId.containsKey(definition.id())) {
            throw new IllegalArgumentException("Duplicate ability id: " + definition.id());
        }
        if (byCombo.containsKey(definition.combo())) {
            throw new IllegalArgumentException("Duplicate ability combo: " + definition.combo());
        }

        byId.put(definition.id(), definition);
        byCombo.put(definition.combo(), definition);
    }

    public Optional<AbilityDefinition> find(AbilityId id) {
        return Optional.ofNullable(byId.get(Objects.requireNonNull(id, "id")));
    }

    public Optional<AbilityDefinition> findExact(List<CombatInput> inputs) {
        Objects.requireNonNull(inputs, "inputs");
        if (inputs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(byCombo.get(new InputCombo(inputs)));
    }

    public boolean hasPrefix(List<CombatInput> inputs) {
        Objects.requireNonNull(inputs, "inputs");
        return byCombo.keySet().stream().anyMatch(combo -> combo.startsWith(inputs));
    }

    public Collection<AbilityDefinition> definitions() {
        return List.copyOf(byId.values());
    }
}

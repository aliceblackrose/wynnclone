package io.github.aliceblackrose.wynnclone.ability;

import io.github.aliceblackrose.wynnclone.combat.AbilityCatalog;
import io.github.aliceblackrose.wynnclone.combat.AbilityDefinition;
import io.github.aliceblackrose.wynnclone.combat.AbilityId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CombatAbilities {
    private final AbilityCatalog catalog;
    private final Map<AbilityId, AbilityAction> actions = new HashMap<>();

    public CombatAbilities(AbilityCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public void register(AbilityDefinition definition, AbilityAction action) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(action, "action");
        if (actions.containsKey(definition.id())) {
            throw new IllegalArgumentException("Duplicate ability action: " + definition.id());
        }
        catalog.register(definition);
        actions.put(definition.id(), action);
    }

    public Optional<AbilityDefinition> definition(AbilityId abilityId) {
        return catalog.find(abilityId);
    }

    public Optional<AbilityAction> action(AbilityId abilityId) {
        return Optional.ofNullable(actions.get(Objects.requireNonNull(abilityId, "abilityId")));
    }
}

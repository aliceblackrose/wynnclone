package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class BasicAttackService {
    private final CombatStateStore states;
    private final CombatClock clock;
    private final WeaponDefinitionResolver weapons;
    private final CombatDamageService damage;

    public BasicAttackService(
            CombatStateStore states,
            CombatClock clock,
            WeaponDefinitionResolver weapons,
            CombatDamageService damage) {
        this.states = Objects.requireNonNull(states, "states");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.weapons = Objects.requireNonNull(weapons, "weapons");
        this.damage = Objects.requireNonNull(damage, "damage");
    }

    public BasicAttackResult attack(Player attacker, LivingEntity target) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(target, "target");

        if (!target.isValid() || target.isDead() || target == attacker) {
            return BasicAttackResult.INVALID_TARGET;
        }

        var state = states.state(attacker.getUniqueId());
        WeaponDefinition weapon = weapons.resolve(attacker.getInventory().getItemInMainHand().getType());
        long now = clock.nowMillis();

        if (!state.basicAttackCooldown().ready(now)) {
            return BasicAttackResult.COOLDOWN;
        }

        state.basicAttackCooldown().trigger(now, weapon.attackSpeed().intervalMillis());

        DamageRequest request = DamageRequest.physical(weapon.baseDamage())
                .withCriticalChanceBonus(weapon.criticalChanceBonus());
        damage.damage(attacker, target, request);

        return BasicAttackResult.HIT;
    }
}

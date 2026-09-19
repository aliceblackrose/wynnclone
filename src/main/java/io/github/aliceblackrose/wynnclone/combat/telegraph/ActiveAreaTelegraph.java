package io.github.aliceblackrose.wynnclone.combat.telegraph;

import io.github.aliceblackrose.wynnclone.combat.projectile.Vec3;
import java.util.Objects;
import java.util.UUID;

record ActiveAreaTelegraph(
        UUID ownerId,
        UUID worldId,
        Vec3 center,
        long impactAtMillis,
        AreaTelegraphSpec spec) {

    ActiveAreaTelegraph {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(spec, "spec");
    }
}

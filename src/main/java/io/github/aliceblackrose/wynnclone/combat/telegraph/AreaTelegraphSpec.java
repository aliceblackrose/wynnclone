package io.github.aliceblackrose.wynnclone.combat.telegraph;

import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactRequest;
import java.util.Objects;
import org.bukkit.Particle;

public record AreaTelegraphSpec(
        long delayMillis,
        Particle warningParticle,
        Particle impactParticle,
        AreaImpactRequest impact) {

    public AreaTelegraphSpec {
        if (delayMillis <= 0L) {
            throw new IllegalArgumentException("Telegraph delay must be positive");
        }
        Objects.requireNonNull(warningParticle, "warningParticle");
        Objects.requireNonNull(impactParticle, "impactParticle");
        Objects.requireNonNull(impact, "impact");
    }
}

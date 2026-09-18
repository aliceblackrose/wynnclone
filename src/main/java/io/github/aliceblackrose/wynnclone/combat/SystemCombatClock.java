package io.github.aliceblackrose.wynnclone.combat;

public final class SystemCombatClock implements CombatClock {

    @Override
    public long nowMillis() {
        return System.nanoTime() / 1_000_000L;
    }
}

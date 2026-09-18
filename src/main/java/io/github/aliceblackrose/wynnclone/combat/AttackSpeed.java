package io.github.aliceblackrose.wynnclone.combat;

public enum AttackSpeed {
    SUPER_SLOW(1200L),
    VERY_SLOW(1000L),
    SLOW(800L),
    NORMAL(650L),
    FAST(500L),
    VERY_FAST(400L),
    SUPER_FAST(325L);

    private final long intervalMillis;

    AttackSpeed(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public long intervalMillis() {
        return intervalMillis;
    }
}

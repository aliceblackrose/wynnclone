package io.github.aliceblackrose.wynnclone.combat;

public final class AttackCooldown {
    private long readyAtMillis;

    public boolean ready(long nowMillis) {
        return remainingMillis(nowMillis) == 0L;
    }

    public long remainingMillis(long nowMillis) {
        if (readyAtMillis <= nowMillis) {
            return 0L;
        }
        return readyAtMillis - nowMillis;
    }

    public void trigger(long nowMillis, long intervalMillis) {
        if (intervalMillis < 0L) {
            throw new IllegalArgumentException("Attack interval must be non-negative");
        }

        try {
            readyAtMillis = Math.addExact(nowMillis, intervalMillis);
        } catch (ArithmeticException exception) {
            readyAtMillis = Long.MAX_VALUE;
        }
    }

    public void reset() {
        readyAtMillis = 0L;
    }
}

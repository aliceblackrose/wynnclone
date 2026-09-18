package io.github.aliceblackrose.wynnclone.combat;

public final class ManaPool {

    private final double maximum;
    private double current;

    public ManaPool(double maximum) {
        if (!Double.isFinite(maximum) || maximum <= 0.0) {
            throw new IllegalArgumentException("Maximum mana must be finite and positive");
        }
        this.maximum = maximum;
        this.current = maximum;
    }

    public double current() {
        return current;
    }

    public double maximum() {
        return maximum;
    }

    public boolean canSpend(double amount) {
        validateAmount(amount);
        return current >= amount;
    }

    public boolean spend(double amount) {
        validateAmount(amount);
        if (current < amount) {
            return false;
        }
        current -= amount;
        return true;
    }

    public void restore(double amount) {
        validateAmount(amount);
        current = Math.min(maximum, current + amount);
    }

    public void setCurrent(double amount) {
        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("Mana must be finite");
        }
        current = Math.clamp(amount, 0.0, maximum);
    }

    private static void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 0.0) {
            throw new IllegalArgumentException("Mana amount must be finite and non-negative");
        }
    }
}

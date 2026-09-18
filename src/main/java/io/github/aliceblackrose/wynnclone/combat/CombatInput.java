package io.github.aliceblackrose.wynnclone.combat;

public enum CombatInput {
    LEFT("L"),
    RIGHT("R");

    private final String symbol;

    CombatInput(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}

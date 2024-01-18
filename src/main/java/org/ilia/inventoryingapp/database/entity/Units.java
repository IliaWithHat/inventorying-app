package org.ilia.inventoryingapp.database.entity;

public enum Units {
    PIECE("Pc"),
    GRAM("G"),
    KILOGRAM("Kg"),
    TON("T"),
    LITRE("L"),
    METER("M"),
    METER2("M2"),
    PAIR("Pair"),
    ROLL("Roll");

    private final String name;

    Units(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

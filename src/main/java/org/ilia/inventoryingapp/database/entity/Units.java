package org.ilia.inventoryingapp.database.entity;

public enum Units {
    PIECE("Piece"),
    KILOGRAM("Kilogram"),
    TON("Ton"),
    LITRE("Litre"),
    METER("Meter"),
    METER2("Meter2"),
    PACKAGE("Package"),
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

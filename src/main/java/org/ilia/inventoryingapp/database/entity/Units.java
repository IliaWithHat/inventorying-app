package org.ilia.inventoryingapp.database.entity;

public enum Units {
    PIECE("Piece"),
    KILOGRAM("Kilogram"),
    LITRE("Litre");

    private final String name;

    Units(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

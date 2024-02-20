package org.ilia.inventoryingapp.database.entity;

public enum Unit {

    PC("Pc"),
    G("G"),
    KG("Kg"),
    T("T"),
    L("L"),
    M("M"),
    M2("M2"),
    PAIR("Pair"),
    ROLL("Roll");

    private final String name;

    Unit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

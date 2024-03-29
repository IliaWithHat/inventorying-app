package org.ilia.inventoryingapp.database.entity;

public enum OptionsForIsOwnedByEmployee {

    IGNORE("Ignore"),
    YES("Yes"),
    NO("No");

    private final String name;

    OptionsForIsOwnedByEmployee(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

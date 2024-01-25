package org.ilia.inventoryingapp.filter;

public enum TimeDurationEnum {
    IGNORE("Ignore"),
    ONE_DAY("1 day"),
    THREE_DAYS("3 days"),
    ONE_WEEK("1 week"),
    TWO_WEEKS("2 weeks"),
    ONE_MONTH("1 month"),
    TREE_MONTHS("3 months"),
    SIX_MONTHS("6 months"),
    ONE_YEAR("1 year");

    private final String name;

    TimeDurationEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

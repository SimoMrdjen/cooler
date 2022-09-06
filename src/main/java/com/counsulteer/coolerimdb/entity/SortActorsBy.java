package com.counsulteer.coolerimdb.entity;

public enum SortActorsBy {
    ID("id"),
    NAME("fullName"),
    BIRTH("birthday");
    private String value;

    SortActorsBy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

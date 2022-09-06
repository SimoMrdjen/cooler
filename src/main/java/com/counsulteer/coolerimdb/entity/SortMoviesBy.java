package com.counsulteer.coolerimdb.entity;

public enum SortMoviesBy {
    ID("id"),
    CREATION("dateOfCreation"),
    RATING("rating"),
    TITLE("title");
    public final String value;

    SortMoviesBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


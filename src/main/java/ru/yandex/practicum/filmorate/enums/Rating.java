package ru.yandex.practicum.filmorate.enums;

public enum Rating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String label;

    private Rating(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
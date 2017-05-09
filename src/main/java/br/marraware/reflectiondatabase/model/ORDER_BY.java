package br.marraware.reflectiondatabase.model;

/**
 * Created by joaogabrielsilvamarra on 06/05/17.
 */

public enum ORDER_BY {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String name;

    ORDER_BY(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}

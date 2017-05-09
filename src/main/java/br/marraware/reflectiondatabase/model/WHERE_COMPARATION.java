package br.marraware.reflectiondatabase.model;

/**
 * Created by joaogabrielsilvamarra on 06/05/17.
 */

public enum WHERE_COMPARATION {
    EQUAL("="),
    NOTEQUAL("!="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    CONTAINS("CONTAINS"),
    MORE_THAN(">"),
    MORE_EQUAL(">="),
    LESS_THAN("<"),
    LESS_EQUAL("<=");

    private String name;

    WHERE_COMPARATION(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}

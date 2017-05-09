package br.marraware.reflectiondatabase.model;

/**
 * Created by joaogabrielsilvamarra on 06/05/17.
 */

public enum NODE_TREE_COMPARATION {
    AND("AND"),
    OR("OR");

    private String name;

    NODE_TREE_COMPARATION(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}

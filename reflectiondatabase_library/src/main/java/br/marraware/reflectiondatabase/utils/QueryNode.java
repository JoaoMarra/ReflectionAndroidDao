package br.marraware.reflectiondatabase.utils;

import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class QueryNode {

    private String column;
    private Object value;
    private WHERE_COMPARATION comparation;

    public QueryNode(String column, Object value, WHERE_COMPARATION comparation) {
        this.column = column;
        this.value = value;
        this.comparation = comparation;
    }

    @Override
    public String toString() {
        return " "+column+" "+comparation+" "+(value instanceof String?"\""+value+"\"":value);
    }
}

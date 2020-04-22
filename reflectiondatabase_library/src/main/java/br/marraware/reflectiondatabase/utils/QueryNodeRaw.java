package br.marraware.reflectiondatabase.utils;

import java.util.Date;

import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class QueryNodeRaw extends QueryNode {

    private String query;

    public QueryNodeRaw(String query) {
        super(null, null, WHERE_COMPARATION.EQUAL);
        this.query = query;
    }

    @Override
    public String toString() {
        return " "+query;
    }
}

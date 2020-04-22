package br.marraware.reflectiondatabase.utils;

import java.util.Date;

import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class QueryNodeBetween extends QueryNode {

    private String column;
    private Object value1, value2;

    public QueryNodeBetween(String column, Object value1, Object value2) {
        super(column, value1, WHERE_COMPARATION.EQUAL);
        this.column = column;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        String value1String;
        if(value1 instanceof Date) {
            value1String = String.format("'%s'", DaoHelper.dateToString((Date) value1));
        } else if (value1 instanceof String){
            value1String = String.format("'%s'", value1);
        } else {
            value1String = value1.toString();
        }
        String value2String;
        if(value2 instanceof Date) {
            value2String = String.format("'%s'", DaoHelper.dateToString((Date) value2));
        } else if (value2 instanceof String){
            value2String = String.format("'%s'", value2);
        } else {
            value2String = value2.toString();
        }

        return " "+column+" BETWEEN "+value1String+" AND "+value2String;
    }
}

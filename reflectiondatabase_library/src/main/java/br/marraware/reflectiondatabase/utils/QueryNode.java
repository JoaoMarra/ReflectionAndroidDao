package br.marraware.reflectiondatabase.utils;

import android.text.format.DateUtils;

import java.util.Date;

import br.marraware.reflectiondatabase.helpers.DaoHelper;
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
        Object valueObj = value;
        if(value instanceof Boolean) {
            Boolean valueBol = (Boolean) value;
            valueObj = (valueBol?1:0);
        } else if(value instanceof Date) {
            valueObj = DaoHelper.dateToString((Date) value);
        }
        return " "+column+" "+comparation+" "+(valueObj instanceof String?"\""+valueObj+"\"":valueObj);
    }
}

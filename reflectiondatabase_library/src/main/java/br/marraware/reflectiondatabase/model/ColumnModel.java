package br.marraware.reflectiondatabase.model;

/**
 * Created by joao_gabriel on 2020-04-22.
 */

import android.database.Cursor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.marraware.reflectiondatabase.helpers.DaoHelper;

/**
 * Represente a modelless object for distinct query
 */
public class ColumnModel extends DaoModel {

    private Class daoModelClass;
    private Map<String, Object> values;

    public ColumnModel(Class daoModelClass, Cursor cursor) {
        this.daoModelClass = daoModelClass;
        int columnCount = cursor.getColumnCount();
        values = new HashMap<>();
        int type;
        Date dateAux;
        for(int i=0; i < columnCount; i++) {
            type = cursor.getType(i);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    values.put(cursor.getColumnName(i),cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    values.put(cursor.getColumnName(i),cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    dateAux = DaoHelper.stringToDate(cursor.getString(i));
                    if(dateAux != null)
                        values.put(cursor.getColumnName(i),dateAux);
                    else
                        values.put(cursor.getColumnName(i),cursor.getString(i));
                    break;
            }
        }
    }

    public Class<DaoModel> getDaoModelClass() {
        return daoModelClass;
    }

    public Object getValue(String column) {
        return values.get(column);
    }

    public int columCount() {
        if(values != null)
            return values.keySet().size();
        return 0;
    }
}

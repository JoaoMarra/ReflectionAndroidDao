package br.marraware.reflectiondatabase.model;

/**
 * Created by joao_gabriel on 2020-04-22.
 */

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject jsonObject;
        JSONArray jsonArray;
        String string;
        for(int i=0; i < columnCount; i++) {
            type = cursor.getType(i);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    values.put(cursor.getColumnName(i),cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    values.put(cursor.getColumnName(i),cursor.getDouble(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    string = cursor.getString(i);
                    dateAux = DaoHelper.stringToDate(string);
                    if(dateAux != null) {
                        values.put(cursor.getColumnName(i),dateAux);
                    } else {
                        try {
                            jsonObject = new JSONObject(string);
                            values.put(cursor.getColumnName(i), jsonObject);
                        } catch (JSONException e) {
                            try {
                                jsonArray = new JSONArray(string);
                                values.put(cursor.getColumnName(i), jsonArray);
                            } catch (JSONException e2) {
                                values.put(cursor.getColumnName(i),string);
                            }
                        }
                    }
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

    public int getInt(String column) {
        return (int) values.get(column);
    }

    public double getDouble(String column) {
        return (double) values.get(column);
    }

    public Date getDate(String column) {
        return (Date) values.get(column);
    }

    public String getString(String column) {
        return values.get(column).toString();
    }

    public JSONObject getJSONObject(String column) {
        return (JSONObject) values.get(column);
    }

    public JSONArray getJSONArray(String column) {
        return (JSONArray) values.get(column);
    }

    public int columCount() {
        if(values != null)
            return values.keySet().size();
        return 0;
    }
}

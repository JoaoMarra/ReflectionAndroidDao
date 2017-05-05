package br.marraware.reflectiondatabase.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

import br.marraware.reflectiondatabase.DataBaseQueryBuilder;
import br.marraware.reflectiondatabase.DataBaseTransaction;
import br.marraware.reflectiondatabase.DataBaseTransactionCallBack;
import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.helpers.DaoHelper;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public abstract class DaoAbstractModel {

    public abstract String identifierColumn();
    public abstract Object identifierValue();

    public DaoAbstractModel() {
    }

    public DaoAbstractModel(JSONObject json) throws JSONException, IllegalAccessException {
        Field[] fields = getFields();
        String name;
        for (int i = 0; i < fields.length; i++) {
            name = fields[i].getName();
            if(json.has(name)) {
                fields[i].set(this, json.get(name));
            }
        }
    }

    public final void configureWithCursor(Cursor cursor) {
        Field[] fields = getFields();
        Class type;
        try {
            int index;
            for (int i = 0; i < fields.length; i++) {
                type = fields[i].getType();
                index = cursor.getColumnIndex(fields[i].getName());
                if(type.isInstance(new String())) {
                    fields[i].set(this, cursor.getString(index));
                } else if(type.isInstance(new Integer(0))) {

                    fields[i].set(this, cursor.getInt(index));
                } else if(type.isInstance(new Float(0))) {
                    fields[i].set(this, cursor.getFloat(index));
                } else if(type.isInstance(new Double(0))) {
                    fields[i].set(this, cursor.getDouble(index));
                } else if(type.isInstance(new Long(0))) {
                    fields[i].set(this, cursor.getLong(index));
                } else if(type.isInstance(new Boolean(true))) {
                    if(cursor.getInt(index) == 1)
                        fields[i].set(this, true);
                    else
                        fields[i].set(this, false);
                }  else if(type.isInstance(new Date())) {
                    fields[i].set(this, DaoHelper.stringToDate(cursor.getString(index)));
                }
            }
        } catch (Exception e){}
    }

    private Field[] getFields() {
        Class c = getClass();
        return c.getDeclaredFields();
    }

    public void save() {
        SQLiteDatabase db = ReflectionDatabaseManager.db();
        ContentValues values = new ContentValues();
        Field[] fields = getFields();
        Class type;
        try {
            for (int i = 0; i < fields.length; i++) {
                type = fields[i].getType();
                if (type.isInstance(new String())) {
                    values.put(fields[i].getName(), (String) fields[i].get(this));
                } else if (type.isInstance(new Integer(0))) {
                    values.put(fields[i].getName(), (Integer) fields[i].get(this));
                } else if (type.isInstance(new Float(0))) {
                    values.put(fields[i].getName(), (Float) fields[i].get(this));
                } else if (type.isInstance(new Double(0))) {
                    values.put(fields[i].getName(), (Double) fields[i].get(this));
                } else if (type.isInstance(new Long(0))) {
                    values.put(fields[i].getName(), (Long) fields[i].get(this));
                } else if (type.isInstance(new Boolean(true))) {
                    if (fields[i].getBoolean(this))
                        values.put(fields[i].getName(), 1);
                    else
                        values.put(fields[i].getName(), 0);
                } else if (type.isInstance(new Date())) {
                    values.put(fields[i].getName(), DaoHelper.dateToString((Date) fields[i].get(this)));
                }
            }
            db.insertWithOnConflict(tableName(this.getClass()), null, values,CONFLICT_REPLACE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete() {
        SQLiteDatabase db = ReflectionDatabaseManager.db();
        db.delete(tableName(this.getClass()), identifierColumn()+"=?",new String[]{""+identifierValue()});
    }

    public void update() {
        SQLiteDatabase db = ReflectionDatabaseManager.db();
        ContentValues values = new ContentValues();
        Field[] fields = getFields();
        Class type;
        String dateString;
        try {
            for (int i = 0; i < fields.length; i++) {
                type = fields[i].getType();
                if(type.isInstance(new String())) {
                    values.put(fields[i].getName(), (String) fields[i].get(this));
                } else if(type.isInstance(new Integer(0))) {
                    values.put(fields[i].getName(), (Integer) fields[i].get(this));
                } else if(type.isInstance(new Float(0))) {
                    values.put(fields[i].getName(), (Float) fields[i].get(this));
                } else if(type.isInstance(new Double(0))) {
                    values.put(fields[i].getName(), (Double) fields[i].get(this));
                } else if(type.isInstance(new Long(0))) {
                    values.put(fields[i].getName(), (Long) fields[i].get(this));
                } else if(type.isInstance(new Boolean(true))) {
                    if(fields[i].getBoolean(this))
                        values.put(fields[i].getName(),1);
                    else
                        values.put(fields[i].getName(),0);
                }  else if(type.isInstance(new Date())) {
                    dateString = DaoHelper.dateToString((Date) fields[i].get(this));
                    if(dateString !=  null)
                        values.put(fields[i].getName(), dateString);
                    else
                        values.putNull(fields[i].getName());
                }
            }
            Object identifier = identifierValue();
            db.update(tableName(this.getClass()),values, identifierColumn()+"=?", new String[]{""+identifier});
        } catch (Exception e){}
    }

    public int rowCount() {
        SQLiteDatabase db = ReflectionDatabaseManager.db();
        Cursor cursor = db.rawQuery("select * from "+tableName(this.getClass()), null);
        return cursor.getCount();
    }

    public final static String tableName(Class c) {
        return c.getSimpleName();
    }
}

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

    public static final String DEFAULT_ID_COLUMN_NAME = "REFLECTION_DAO_ID";
    public String ID_COLMUN_NAME;
    public Object REFLECTION_DAO_ID;

    public final String identifierColumn() {
        if(ID_COLMUN_NAME == null) {
            Field[] fields = getFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    ID_COLMUN_NAME = field.getName();
                    return ID_COLMUN_NAME;
                }
            }
            if(ID_COLMUN_NAME == null)
                ID_COLMUN_NAME = DEFAULT_ID_COLUMN_NAME;
        }
        return ID_COLMUN_NAME;
    }

    public final Object identifierValue() {
        if(REFLECTION_DAO_ID == null) {
            Field[] fields = getFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    try {
                        REFLECTION_DAO_ID = field.get(this);
                        return REFLECTION_DAO_ID;
                    } catch (Exception e){}
                }
            }
            if(REFLECTION_DAO_ID == null)
                REFLECTION_DAO_ID = -1L;
        }
        return REFLECTION_DAO_ID;
    }

    private void setDefaultIdentifier(long id) {
        if(identifierColumn().compareTo(DEFAULT_ID_COLUMN_NAME) == 0)
            REFLECTION_DAO_ID = id;
    }

    public DaoAbstractModel() {}

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

    public void configureWithCursor(Cursor cursor) {
        Field[] fields = getFields();
        Class type;
        boolean getKey = false;
        int index;
        try {
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
                if(fields[i].isAnnotationPresent(PrimaryKey.class)) {
                    getKey = true;
                }
            }
        } catch (Exception e){}
        if(!getKey) {
            index = cursor.getColumnIndex(DEFAULT_ID_COLUMN_NAME);
            if(index != -1)
                setDefaultIdentifier(cursor.getLong(index));
        }
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
        boolean getKey = false;
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
                if(fields[i].isAnnotationPresent(PrimaryKey.class)) {
                    getKey = true;
                }
            }
            if(!getKey) {
                if((Long)identifierValue() != -1)
                    values.put(identifierColumn(), (Long)identifierValue());
            }
            long id = db.insertWithOnConflict(tableName(this.getClass()), null, values,CONFLICT_REPLACE);

            if(!getKey) {
                if((Long)identifierValue() == -1) {
                    setDefaultIdentifier(id);
                }
            }
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

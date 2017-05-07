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
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import br.marraware.reflectiondatabase.DataBaseQueryBuilder;
import br.marraware.reflectiondatabase.DataBaseTransaction;
import br.marraware.reflectiondatabase.DataBaseTransactionCallBack;
import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.ReflectionDatabaseQuery;
import br.marraware.reflectiondatabase.helpers.DaoHelper;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public abstract class DaoAbstractModel {

    public static final String DEFAULT_ID_COLUMN_NAME = "REFLECTION_DAO_ID";
    private String ID_COLMUN_NAME;
    private Object REFLECTION_DAO_ID;

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
        } catch (Exception e){
            e.printStackTrace();
        }
        if(!getKey) {
            index = cursor.getColumnIndex(DEFAULT_ID_COLUMN_NAME);
            if(index != -1)
                setDefaultIdentifier(cursor.getLong(index));
        }

        ArrayList<Field> depedencies = getDependecyValues();
        TableDepedency depedency;
        ParameterizedType parameterizedType;
        try {
            for (Field field : depedencies) {
                depedency = field.getAnnotation(TableDepedency.class);
                if (DaoAbstractModel.class.isAssignableFrom(field.getType())) {
                    field.set(this,
                            ReflectionDatabaseQuery.get(field.getType(),
                            new DataBaseQueryBuilder().where(depedency.value(),identifierValue(), DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL)));
                } else if (List.class.isAssignableFrom(field.getType())) {
                    parameterizedType = (ParameterizedType) field.getGenericType();
                    if(parameterizedType != null) {
                        type = (Class) parameterizedType.getActualTypeArguments()[0];
                        field.set(this,
                                ReflectionDatabaseQuery.getAll(type,
                                        new DataBaseQueryBuilder().where(depedency.value(), identifierValue(), DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Class<? extends DaoAbstractModel>,String> getDepedencyTables() {
        HashMap<Class<? extends DaoAbstractModel>,String> tables = new HashMap<>();
        Field[] fields = getFields();
        Field field;
        Class type;
        TableDepedency depedency;
        ParameterizedType parameterizedType;
        try {
            for (int i = 0; i < fields.length; i++) {
                field = fields[i];
                type = null;
                if(field.isAnnotationPresent(TableDepedency.class)) {
                    depedency = field.getAnnotation(TableDepedency.class);
                    if (DaoAbstractModel.class.isAssignableFrom(field.getType())) {
                        type = field.getType();
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        parameterizedType = (ParameterizedType) field.getGenericType();
                        if(parameterizedType != null) {
                            type = (Class) parameterizedType.getActualTypeArguments()[0];
                        }
                    }
                    if(type != null && DaoAbstractModel.class.isAssignableFrom(type)) {
                        tables.put(type,depedency.value());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tables;
    }

    public ArrayList<Field> getDependecyValues() {
        ArrayList<Field> objects = new ArrayList<>();
        Field[] fields = getFields();
        Field field;
        Class type;
        ParameterizedType parameterizedType;
        try {
            for (int i = 0; i < fields.length; i++) {
                field = fields[i];
                type = null;
                if(field.isAnnotationPresent(TableDepedency.class)) {
                    if (DaoAbstractModel.class.isAssignableFrom(field.getType())) {
                        type = field.getType();
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        parameterizedType = (ParameterizedType) field.getGenericType();
                        if(parameterizedType != null)
                            type = (Class) parameterizedType.getActualTypeArguments()[0];
                    }
                    if(type != null && DaoAbstractModel.class.isAssignableFrom(type)) {
                        objects.add(field);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
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

            saveDependecies(identifierValue());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete() {
        SQLiteDatabase db = ReflectionDatabaseManager.db();
        db.delete(tableName(this.getClass()), identifierColumn()+"=?",new String[]{""+identifierValue()});
        deleteDependecies();
        setDefaultIdentifier(-1);
    }

    public void deleteDependecies() {
        HashMap<Class<? extends DaoAbstractModel>,String> depedencies = getDepedencyTables();
        if(!depedencies.isEmpty()) {
            SQLiteDatabase db = ReflectionDatabaseManager.db();
            Iterator<Class<? extends DaoAbstractModel>> iterator = depedencies.keySet().iterator();
            Class<? extends DaoAbstractModel> type;
            String key;
            int qtd;
            while (iterator.hasNext()) {
                type = iterator.next();
                key = depedencies.get(type);
                qtd = db.delete(DaoAbstractModel.tableName(type), key+" =?",new String[]{""+identifierValue()});
                Log.d(type.getSimpleName(),"DELETE DEPENDENCY - "+qtd);
            }
        }
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

            saveDependecies(identifier);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveDependecies(Object idValue) {
        ArrayList<Field> depedencies = getDependecyValues();
        DaoAbstractModel model;
        ArrayList<DaoAbstractModel> modelList;
        TableDepedency depedency;
        try {
            for (Field field : depedencies) {
                depedency = field.getAnnotation(TableDepedency.class);
                if (DaoAbstractModel.class.isAssignableFrom(field.getType())) {
                    model = (DaoAbstractModel) field.get(this);
                    if (model != null) {
                        model.getClass().getDeclaredField(depedency.value()).set(model,idValue);
                        model.save();
                        Log.d(model.getClass().getSimpleName(), "SAVE DEPENDENCY - "+model.identifierValue());
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    modelList = (ArrayList<DaoAbstractModel>) field.get(this);
                    if(modelList != null) {
                        for (DaoAbstractModel m : modelList) {
                            m.getClass().getDeclaredField(depedency.value()).set(m,idValue);
                            m.save();
                            Log.d(m.getClass().getSimpleName(), "SAVE DEPENDENCY - "+m.identifierValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

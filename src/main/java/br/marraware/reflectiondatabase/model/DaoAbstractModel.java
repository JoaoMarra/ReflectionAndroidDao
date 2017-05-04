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
import java.util.ArrayList;
import java.util.Date;

import br.marraware.reflectiondatabase.DataBaseHelper;
import br.marraware.reflectiondatabase.DataBaseQueryBuilder;
import br.marraware.reflectiondatabase.DataBaseTransaction;
import br.marraware.reflectiondatabase.DataBaseTransactionCallBack;
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

    public DaoAbstractModel(SQLiteCursor cursor) {
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

    public static void saveAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.SAVE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                callBack.onBack(models);
            }

            @Override
            public void onFailure(String errorMessage) {
                callBack.onFailure(errorMessage);
            }
        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }

    public void save() {
        SQLiteDatabase db = DataBaseHelper.db();
        ContentValues values = new ContentValues();
        Field[] fields = getFields();
        Class type;
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
                    values.put(fields[i].getName(), DaoHelper.dateToString((Date) fields[i].get(this)));
                }
            }
            db.insertWithOnConflict(abstractTableName(tableName()), null, values,CONFLICT_REPLACE);
        } catch (Exception e){}
    }

    public static void deleteAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.DELETE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                callBack.onBack(models);
            }

            @Override
            public void onFailure(String errorMessage) {
                callBack.onFailure(errorMessage);
            }
        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }

    public void delete() {
        SQLiteDatabase db = DataBaseHelper.db();
        db.delete(abstractTableName(tableName()), identifierColumn()+"=?",new String[]{""+identifierValue()});
    }

    public void get(DataBaseTransactionCallBack callBack) {
        get(new DataBaseQueryBuilder(), callBack);
    }

    public void get(DataBaseQueryBuilder queryBuilder, final DataBaseTransactionCallBack callBack) {
        ArrayList list = new ArrayList();
        queryBuilder.setTableName(abstractTableName(tableName()));
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.GET, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                ArrayList models = new ArrayList();
                try {
                    Constructor constructor = DaoAbstractModel.this.getClass().getConstructor(SQLiteCursor.class);
                    if(cursor.moveToFirst()) {
                        while ( ! cursor.isAfterLast()) {
                            models.add(constructor.newInstance(cursor));
                            cursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                callBack.onBack(models);
            }

            @Override
            public void onFailure(String errorMessage) {
                callBack.onFailure(errorMessage);
            }
        });
        transaction.setQueryBuilder(queryBuilder);
        transaction.execute();
    }

    public void update() {
        SQLiteDatabase db = DataBaseHelper.db();
        ContentValues values = new ContentValues();
        Field[] fields = getFields();
        Class type;
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
                    values.put(fields[i].getName(), DaoHelper.dateToString((Date) fields[i].get(this)));
                }
            }
            Object identifier = identifierValue();
            db.update(abstractTableName(tableName()),values, identifierColumn()+"=?", new String[]{""+identifier});
        } catch (Exception e){}
    }

    public static void updateAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.UPDATE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                callBack.onBack(models);
            }

            @Override
            public void onFailure(String errorMessage) {
                callBack.onFailure(errorMessage);
            }
        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }

    public abstract String tableName();

    private final static String abstractTableName(String tableName) {
        return tableName+"DAO";
    }

    public final void createTable(SQLiteDatabase db) {
        Field[] fields = getFields();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(abstractTableName(tableName())+"(");
        Class type;
        String name, typeString;
        try {
            for (int i = 0; i < fields.length; i++) {
                name = fields[i].getName();
                type = fields[i].getType();
                typeString = null;
                if(type.isInstance(new String())) {
                    typeString = "varchar(255)";
                }
                else if(type.isInstance(new Integer(0))) {
                    typeString = "int";
                } else if(type.isInstance(new Float(0))) {
                    typeString = "double";
                } else if(type.isInstance(new Double(0))) {
                    typeString = "double";
                } else if(type.isInstance(new Long(0))) {
                    typeString = "bigint";
                } else if(type.isInstance(new Boolean(true))) {
                    typeString = "int";
                } else if(type.isInstance(new Date())) {
                    typeString = "datetime";
                }
                if(name.compareTo(identifierColumn()) == 0) {
                    typeString += " primary key";
                }
                if(typeString != null)
                    builder.append(name+" "+typeString+(i == fields.length-1?")":","));
            }
        } catch (Exception e){
            String exec = builder.toString();
            Log.d(tableName(),"ErrorCreateTable:\n"+exec);
        }
        finally {
            String exec = builder.toString();
            if(exec.charAt(exec.length()-1) == ',')
                exec = exec.substring(0,exec.length()-1)+")";
            Log.d(tableName(),"DaoCreateTable:\n"+exec);
            db.execSQL(exec);
        }

    }
    public final void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+abstractTableName(tableName()));
    }
}

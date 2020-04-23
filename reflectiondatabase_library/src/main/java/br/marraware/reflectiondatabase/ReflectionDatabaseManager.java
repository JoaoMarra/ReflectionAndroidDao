package br.marraware.reflectiondatabase;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.PrimaryKey;
import br.marraware.reflectiondatabase.model.Unique;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public final class ReflectionDatabaseManager{

    private static SQLiteOpenHelper dataBaseHelper;

    public final static void initDataBase(SQLiteOpenHelper helper) {
        dataBaseHelper = helper;
        db();
    }

    public static SQLiteOpenHelper getInstance() {
        return dataBaseHelper;
    }

    public static SQLiteDatabase db() {
        return dataBaseHelper.getWritableDatabase();
    }

    /**
     * Creates a new table using the Dao class
     * @param modelClass Dao class extending DaoModel
     * @return true if the table was created, false instead
     */
    public static boolean createTable(Class<? extends DaoModel> modelClass, SQLiteDatabase db) {
        if(dataBaseHelper == null)
            return false;

        StringBuilder builder = new StringBuilder();
        DaoModel model;
        boolean setKey = false;
        try {
            Constructor constructor = modelClass.getConstructor();
            model = (DaoModel) constructor.newInstance();

            Field[] fields = modelClass.getDeclaredFields();
            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(model.tableName(modelClass)+"(");
            Class type;
            String name, typeString;
            for (int i = 0; i < fields.length; i++) {
                name = fields[i].getName();
                type = fields[i].getType();
                typeString = null;
                if(type.isInstance(new String())) {
                    typeString = "text";
                }
                else if(type.isInstance(new Integer(0))) {
                    typeString = "int";
                } else if(type.isInstance(new Float(0))) {
                    typeString = "double";
                } else if(type.isInstance(new Double(0))) {
                    typeString = "double";
                } else if(type.isInstance(new Long(0))) {
                    if(!setKey && fields[i].isAnnotationPresent(PrimaryKey.class)) {
                        typeString = "INTEGER";
                    } else {
                        typeString = "bigint";
                    }
                } else if(type.isInstance(new Boolean(true))) {
                    typeString = "int";
                } else if(type.isInstance(new Date())) {
                    typeString = "datetime";
                }
                if(!setKey && fields[i].isAnnotationPresent(PrimaryKey.class)) {
                    typeString += " primary key";
                    setKey = true;
                }
                if(fields[i].isAnnotationPresent(Unique.class)) {
                    typeString += " UNIQUE";
                }
                if(typeString != null)
                    builder.append("\n"+name+" "+typeString+",");
            }
        } catch (Exception e){
            String exec = builder.toString();
            Log.d(modelClass.getSimpleName(),"ErrorCreateTable:\n"+exec);
            return false;
        }
        finally {
            if(!setKey) {
                builder.append("\n"+DaoModel.DEFAULT_ID_COLUMN_NAME+" INTEGER primary key autoincrement)");
                setKey = true;
            }
            String exec = builder.toString();
            if(exec.charAt(exec.length()-1) == ',')
                exec = exec.substring(0,exec.length()-1)+")";
            Log.d(modelClass.getSimpleName(),"DaoCreateTable:\n"+exec);
            db.execSQL(exec);
        }
        return true;
    }

    /**
     * Drops table using the Dao class
     * @param modelClass Dao class extending DaoModel
     * @return true if the table was created, false instead
     */
    public static boolean dropTable(Class<? extends DaoModel> modelClass, SQLiteDatabase db) {
        if(dataBaseHelper == null)
            return false;
        db.execSQL("DROP TABLE IF EXISTS "+modelClass.getSimpleName());
        return true;
    }
}

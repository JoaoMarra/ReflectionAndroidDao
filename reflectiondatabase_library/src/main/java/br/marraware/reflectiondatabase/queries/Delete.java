package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to delete rows from table
 *
 * execute return nothing
 */
public class Delete extends QueryType {

    private Delete() {

    }

    public static <T extends DaoModel> DeleteQueryTransaction<T> from(Class<T> T) {
        return new DeleteQueryTransaction<T>(T, new Delete());
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException {
        try {
            SQLiteDatabase db = ReflectionDatabaseManager.db();

            String where = whereString();
            Log.d("RelfectionDataBase", "DELETE - where" + where);

            String rawQuery = "select * from " + DaoModel.tableName(modelClass) +
                    (where != null && where.length() > 0 ? " where" + where : "");
            Cursor cursor = db.rawQuery(rawQuery, null);

            Constructor<T> constructor = modelClass.getConstructor();
            T model = constructor.newInstance();

            if (cursor != null && cursor.getCount() > 0) {
                StringBuilder stringId = new StringBuilder();
                int index;
                int type;
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        index = cursor.getColumnIndex(model.identifierColumn());
                        if(index != -1) {
                            type = cursor.getType(index);
                            switch (type) {
                                case Cursor.FIELD_TYPE_FLOAT:
                                    stringId.append(","+cursor.getFloat(index));
                                    break;
                                case Cursor.FIELD_TYPE_INTEGER:
                                    stringId.append(","+cursor.getInt(index));
                                    break;
                                case Cursor.FIELD_TYPE_STRING:
                                    stringId.append(",\""+cursor.getString(index)+"\"");
                                    break;
                                case Cursor.FIELD_TYPE_BLOB:
                                    stringId.append(","+cursor.getBlob(index));
                                    break;
                            }
                        }
                        cursor.moveToNext();
                    }
                }
                stringId.append(")");
                String dependecyIds = stringId.toString();
                dependecyIds = "("+dependecyIds.substring(1);

                Log.d("RelfectionDataBase","DELETE - where"+where);
                int count = db.delete(DaoModel.tableName(modelClass),where,null);

                HashMap<Class<? extends DaoModel>,String> dependecies = model.getDepedencyTables();
                int deletedDependency;
                if(dependecies.size() > 0) {
                    Iterator<Class<? extends DaoModel>> iterator = dependecies.keySet().iterator();
                    Class<? extends DaoModel> classe;
                    String foreignKey;
                    while (iterator.hasNext()) {
                        classe = iterator.next();
                        foreignKey = dependecies.get(classe);

                        Log.d("RelfectionDataBase","DEPENDECY DELETE ("+DaoModel.tableName(classe)+") - where "+foreignKey+" in "+dependecyIds);
                        deletedDependency = db.delete(DaoModel.tableName(classe),foreignKey+" in "+dependecyIds,null);
                        Log.d("RelfectionDataBase","DEPENDECY DELETE ("+DaoModel.tableName(classe)+") - qtd:"+deletedDependency);
                    }
                }

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}

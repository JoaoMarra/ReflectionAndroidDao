package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

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

    public static <T extends DaoModel> QueryTransaction<T> from(Class<T> T) {
        return new QueryTransaction<T>(T, new Delete());
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException {
        SQLiteDatabase db = ReflectionDatabaseManager.db();

        String where = whereString();
        Log.d("RelfectionDataBase","DELETE - where"+where);

        String rawQuery = "select * from "+DaoModel.tableName(modelClass)+
                (where != null && where.length() > 0?" where"+where:"");
        Cursor cursor = db.rawQuery(rawQuery, null);
        ArrayList<T> models = null;
        if(cursor != null) {
            try {
                Constructor<T> constructor = modelClass.getConstructor();
                T model;
                if (cursor.moveToFirst()) {
                    models = new ArrayList();
                    while (!cursor.isAfterLast()) {
                        model = constructor.newInstance();
                        model.configureWithCursor(cursor);
                        models.add(model);
                        cursor.moveToNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(models != null) {
            for(T model : models)
                model.delete();
        }

        return null;
    }
}

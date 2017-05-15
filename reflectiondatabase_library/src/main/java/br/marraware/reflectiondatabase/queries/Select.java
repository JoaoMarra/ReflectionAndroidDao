package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to select rows from table
 *
 * execute return the list of selected models
 */
public class Select extends QueryType {

    private Select() {

    }

    public static <T extends DaoModel> SelectQueryTransaction<T> from(Class<T> T) {
        return new SelectQueryTransaction<T>(T, new Select());
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException {

        SQLiteDatabase db = ReflectionDatabaseManager.db();

        String where = whereString();

        String rawQuery = "select * from "+DaoModel.tableName(modelClass)+
                (where != null && where.length() > 0?" where"+where:"")+
                (orderBy != null?orderBy:"")+
                (limit != -1?" limit "+limit:"");

        Log.d("RelfectionDataBase","SELECT - "+rawQuery);

        return db.rawQuery(rawQuery, null);
    }
}

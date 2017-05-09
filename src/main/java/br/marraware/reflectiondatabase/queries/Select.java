package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public static <T extends DaoModel> QueryTransaction<T> from(Class<T> T) {
        return new QueryTransaction<T>(T, new Select());
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException {

        SQLiteDatabase db = ReflectionDatabaseManager.db();

        String rawQuery = "select * from "+DaoModel.tableName(modelClass)+" where"+whereString()+(orderBy != null?orderBy:"")+(limit != -1?" limit "+limit:"");
        Log.d("RelfectionDataBase","SELEC - "+rawQuery);

        return db.rawQuery(rawQuery, null);
    }
}

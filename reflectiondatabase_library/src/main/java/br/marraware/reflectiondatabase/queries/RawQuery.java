package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.ColumnModel;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to select rows from table
 *
 * execute return the list of selected models
 */
public class RawQuery extends QueryType {

    private String query;

    private RawQuery(String query) {
        this.query = query;
    }

    public static RawQueryTransaction<ColumnModel> query(String query) {
        return new RawQueryTransaction<ColumnModel>(new RawQuery(query));
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit, int offset) throws QueryException {

        SQLiteDatabase db = ReflectionDatabaseManager.db();

        Log.d("RelfectionDataBase","RawQuery - "+query);

        return db.rawQuery(query, null);
    }
}

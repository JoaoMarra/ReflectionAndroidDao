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

        Log.d("RelfectionDataBase","DELETE - where"+whereString());

        db.delete(DaoModel.tableName(modelClass),whereString(), null);

        return null;
    }
}

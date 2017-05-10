package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public class InsertQueryTransaction<T extends DaoModel> extends QueryTransaction<T> {

    public InsertQueryTransaction(Class<T> T, Insert type) {
        super(T, type);
    }

    public InsertQueryTransaction<T> set(String column, Object value) throws ColumnNotFoundException {
        Insert insert = (Insert) type;
        insert.set(column, value, modelClass);

        return this;
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass, null, -1);
    }
}

package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public class DeleteQueryTransaction<T extends DaoModel> extends QueryTransactionWhere<T> {

    public DeleteQueryTransaction(Class<T> T, Delete type) {
        super(T, type);
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass, null, -1);
    }
}

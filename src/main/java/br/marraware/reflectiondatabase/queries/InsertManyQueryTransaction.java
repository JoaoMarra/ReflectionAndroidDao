package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public class InsertManyQueryTransaction<T extends DaoModel> extends QueryTransaction<T> {

    public InsertManyQueryTransaction(Class<T> T, InsertMany<T> type) {
        super(T, type);
    }

    public InsertManyQueryTransaction<T> addModel(T... models) throws ColumnNotFoundException {
        if(type instanceof InsertMany) {
            InsertMany insertAll = (InsertMany) type;
            insertAll.addModel(models);
        }

        return this;
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass, null, -1);
    }
}

package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public class DeleteQueryTransaction<T extends DaoModel> extends QueryTransactionWhere<T> {

    public DeleteQueryTransaction(Class<T> T, Delete type) {
        super(T, type);
    }


    @Override
    public DeleteQueryTransaction where(String column, Object value, WHERE_COMPARATION comparation) throws ColumnNotFoundException {
        super.where(column, value, comparation);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereAnd(Object[]... columnValueComparation) throws ColumnNotFoundException {
        super.whereAnd(columnValueComparation);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereOr(Object[]... columnValueComparation) throws ColumnNotFoundException {
        super.whereOr(columnValueComparation);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereIn(String column, Object... values) throws ColumnNotFoundException {
        super.whereIn(column, values);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereNotIn(String column, Object... values) throws ColumnNotFoundException {
        super.whereNotIn(column, values);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereBetween(String column, Object value1, Object value2) throws ColumnNotFoundException {
        super.whereBetween(column, value1, value2);
        return this;
    }

    @Override
    public DeleteQueryTransaction whereRaw(String query) throws ColumnNotFoundException {
        super.whereRaw(query);
        return this;
    }

    @Override
    public DeleteQueryTransaction<T> whereJSONObject(String column, String key, Object value) throws ColumnNotFoundException {
        super.whereJSONObject(column, key, value);
        return this;
    }

    @Override
    public DeleteQueryTransaction<T> whereJSONArray(String column, Object value) throws ColumnNotFoundException {
        super.whereJSONArray(column, value);
        return this;
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass, null, -1, -1);
    }

    public DeleteQueryTransaction<T> setConflictType(int conflictType) {
        type.setConflictType(conflictType);
        return this;
    }
}

package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import java.util.ArrayList;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.ORDER_BY;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.QueryNode;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public class UpdateQueryTransaction<T extends DaoModel> extends QueryTransactionWhere<T> {

    public UpdateQueryTransaction(Class<T> T, Update type) {
        super(T, type);
    }

    @Override
    public UpdateQueryTransaction<T> where(String column, Object value, WHERE_COMPARATION comparation) throws ColumnNotFoundException {
        super.where(column, value, comparation);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereAnd(Object[]... columnValueComparation) throws ColumnNotFoundException {
        super.whereAnd(columnValueComparation);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereOr(Object[]... columnValueComparation) throws ColumnNotFoundException {
        super.whereOr(columnValueComparation);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereIn(String column, Object... values) throws ColumnNotFoundException {
        super.whereIn(column, values);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereNotIn(String column, Object... values) throws ColumnNotFoundException {
        super.whereNotIn(column, values);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereBetween(String column, Object value1, Object value2) throws ColumnNotFoundException {
        super.whereBetween(column, value1, value2);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereRaw(String query) throws ColumnNotFoundException {
        super.whereRaw(query);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereJSONObject(String column, String key, Object value) throws ColumnNotFoundException {
        super.whereJSONObject(column, key, value);
        return this;
    }

    @Override
    public UpdateQueryTransaction<T> whereJSONArray(String column, Object value) throws ColumnNotFoundException {
        super.whereJSONArray(column, value);
        return this;
    }

    public UpdateQueryTransaction<T> set(String column, Object value) throws ColumnNotFoundException {
        Update update = (Update) type;
        update.set(column, value, modelClass);

        return this;
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass,null, -1, -1);
    }

    public UpdateQueryTransaction<T> setConflictType(int conflictType) {
        type.setConflictType(conflictType);
        return this;
    }
}

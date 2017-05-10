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

    public UpdateQueryTransaction<T> set(String column, Object value) throws ColumnNotFoundException {
        Update update = (Update) type;
        update.set(column, value, modelClass);

        return this;
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass,null, -1);
    }
}

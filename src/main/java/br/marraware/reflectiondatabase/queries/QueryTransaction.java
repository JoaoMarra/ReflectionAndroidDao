package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.ORDER_BY;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.AsyncQueryCallback;
import br.marraware.reflectiondatabase.utils.QueryNode;
import br.marraware.reflectiondatabase.utils.TransactionTask;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class QueryTransaction<T extends DaoModel> {

    private Class<T> modelClass;
    private QueryType type;
    private String orderBy;
    private int limit;

    public QueryTransaction(Class<T> T, QueryType type) {
        this.modelClass = T;
        this.type = type;
        orderBy = null;
        limit = -1;
    }

    public QueryTransaction<T> set(String column, Object value) throws ColumnNotFoundException {
        if(type instanceof Insert) {
            Insert insert = (Insert) type;
            insert.set(column, value, modelClass);
        } else if(type instanceof Update) {
            Update update = (Update) type;
            update.set(column, value, modelClass);
        }

        return this;
    }

    public QueryTransaction<T> where(String column, Object value, WHERE_COMPARATION comparation) throws ColumnNotFoundException {
        if(type instanceof Insert)
            return this;

        type.where(modelClass, column, value, comparation);
        return this;
    }

    public QueryTransaction<T> whereAnd(Object[]... columnValueComparation) throws ColumnNotFoundException {
        if(type instanceof Insert)
            return this;

        String column;
        Object value;
        WHERE_COMPARATION comparation;
        Object[] item;

        ArrayList<QueryNode> nodes = new ArrayList<>();
        for(int i=0; i < columnValueComparation.length; i++) {
            item = columnValueComparation[i];
            if(item != null && item.length == 3) {
                if(item[0] instanceof String) {
                    column = (String) item[0];
                    value = item[1];
                    comparation = (WHERE_COMPARATION) item[2];
                    if(DaoModel.checkColumn(modelClass, column))
                        nodes.add(new QueryNode(column, value, comparation));
                }
            }
        }
        if(nodes.size() > 0) {
            type.whereTree(NODE_TREE_COMPARATION.AND, nodes.toArray(new QueryNode[nodes.size()]));
        }

        return this;
    }

    public QueryTransaction<T> whereOr(Object[]... columnValueComparation) throws ColumnNotFoundException {
        if(type instanceof Insert)
            return this;

        String column;
        Object value;
        WHERE_COMPARATION comparation;
        Object[] item;

        ArrayList<QueryNode> nodes = new ArrayList<>();
        for(int i=0; i < columnValueComparation.length; i++) {
            item = columnValueComparation[i];
            if(item != null && item.length == 3) {
                if(item[0] instanceof String) {
                    column = (String) item[0];
                    value = item[1];
                    comparation = (WHERE_COMPARATION) item[2];
                    if(DaoModel.checkColumn(modelClass, column))
                        nodes.add(new QueryNode(column, value, comparation));
                }
            }
        }
        if(nodes.size() > 0) {
            type.whereTree(NODE_TREE_COMPARATION.OR, nodes.toArray(new QueryNode[nodes.size()]));
        }

        return this;
    }

    public QueryTransaction<T> orderBy(String column, ORDER_BY order) throws ColumnNotFoundException {
        if(type instanceof Insert || type instanceof Update)
            return this;

        if(DaoModel.checkColumn(modelClass, column)) {
            String newOrderBy = " "+column+" "+order;
            if (orderBy == null) {
                orderBy = newOrderBy;
            } else {
                orderBy = orderBy+newOrderBy;
            }
        }

        return this;
    }

    public QueryTransaction<T> limit(int limit) {
        if(type instanceof Insert || type instanceof Update)
            return this;

        this.limit = limit;

        return this;
    }

    public List<T> execute() throws QueryException {
        String newOrderBy = null;
        if(orderBy != null) {
            newOrderBy = " order by "+orderBy;
        }
        Cursor cursor = type.execute(modelClass, newOrderBy, limit);
        ArrayList models = null;
        if(cursor != null) {
            try {
                Constructor<T> constructor = modelClass.getConstructor();
                T model;
                if (cursor.moveToFirst()) {
                    models = new ArrayList();
                    while (!cursor.isAfterLast()) {
                        model = constructor.newInstance();
                        model.configureWithCursor(cursor);
                        models.add(model);
                        cursor.moveToNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return models;
    }

    public T executeForFirst() throws QueryException {
        ArrayList<T> models = (ArrayList<T>) execute();
        if(models != null && models.size() > 0)
            return models.get(0);
        return null;
    }

    public void executeAsync(final AsyncQueryCallback<T> callback) {
        TransactionTask<T> task = new TransactionTask<>(callback, this);
        task.execute();
    }
}

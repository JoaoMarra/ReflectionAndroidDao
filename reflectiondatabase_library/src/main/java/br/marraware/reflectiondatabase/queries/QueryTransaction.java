package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

public abstract class QueryTransaction<T extends DaoModel> {

    protected Class<T> modelClass;
    protected QueryType type;

    public QueryTransaction(Class<T> T, QueryType type) {
        this.modelClass = T;
        this.type = type;
        if(type instanceof Insert) {
            try {
                Constructor<T> constructor = modelClass.getConstructor();
                T model = constructor.newInstance();
                type.setConflictType(model.inserConflict());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(type instanceof Update) {
            try {
                Constructor<T> constructor = modelClass.getConstructor();
                T model = constructor.newInstance();
                type.setConflictType(model.updateConflict());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract Cursor preExecute() throws QueryException;

    private ArrayList<T> abstractPostExecute(Cursor cursor) {
        ArrayList<T> models = null;
        if(cursor != null) {
            try {
                Constructor<T> constructor = modelClass.getConstructor();
                T model;
                if (cursor.moveToFirst()) {
                    models = new ArrayList<>();
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

    public void postExecute(ArrayList<T> models) {

    }

    public ArrayList<T> execute() throws QueryException {

        long now = System.currentTimeMillis();

        Cursor cursor = preExecute();

        ArrayList<T> models = null;
        if(cursor != null)
            models = abstractPostExecute(cursor);

        if(cursor != null && !cursor.isClosed())
            cursor.close();
        if(models != null)
            postExecute(models);

        Log.d("QueryTransaction","BENCHMARK - Query:"+type.getClass().getSimpleName()+" end:"+(System.currentTimeMillis() - now));
        return models;
    }

    public T executeForFirst() throws QueryException {
        ArrayList<T> models = execute();
        if(models != null && models.size() > 0)
            return models.get(0);
        return null;
    }

    public void executeAsync(final AsyncQueryCallback<T> callback) {
        TransactionTask<T> task = new TransactionTask<>(callback, this);
        task.execute();
    }

    public QueryTransaction<T> setConflictType(int conflictType) {
        type.setConflictType(conflictType);
        return this;
    }


}

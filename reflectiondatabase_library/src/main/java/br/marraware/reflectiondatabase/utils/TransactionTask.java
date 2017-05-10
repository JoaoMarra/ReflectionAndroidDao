package br.marraware.reflectiondatabase.utils;

import android.os.AsyncTask;

import java.util.List;

import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.queries.Insert;
import br.marraware.reflectiondatabase.queries.QueryTransaction;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class TransactionTask<T extends DaoModel> extends AsyncTask<Integer, Integer, List<T>> {

    private AsyncQueryCallback<T> callback;
    private QueryTransaction<T> transaction;

    public TransactionTask(AsyncQueryCallback<T> callback, QueryTransaction<T> transaction) {
        this.callback = callback;
        this.transaction = transaction;
    }

    @Override
    protected List<T> doInBackground(Integer... integers) {
        try {
            return transaction.execute();
        } catch (QueryException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<T> ts) {
        if(callback != null)
            callback.onBack(ts);
    }
}

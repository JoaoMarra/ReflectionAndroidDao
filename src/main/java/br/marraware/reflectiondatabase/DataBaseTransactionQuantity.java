package br.marraware.reflectiondatabase;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import br.marraware.reflectiondatabase.model.DaoAbstractModel;
import br.marraware.reflectiondatabase.model.TRANSACTION_METHOD;

/**
 * Created by joaogabrielsilvamarra on 06/05/17.
 */

public class DataBaseTransactionQuantity extends AsyncTask<Integer, Integer, Integer> {

    private InternTransactionQuantityCallBack quantityCallBack;
    private TRANSACTION_METHOD method;
    private DataBaseQueryBuilder queryBuilder;

    public DataBaseTransactionQuantity(TRANSACTION_METHOD method, InternTransactionQuantityCallBack quantityCallBack) {
        this.quantityCallBack = quantityCallBack;
        this.method = method;
    }

    public DataBaseQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(DataBaseQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        int rowCount = 0;
        if(queryBuilder != null) {
            SQLiteDatabase db = ReflectionDatabaseManager.db();
            String where = queryBuilder.getWhereString();
            if(method == TRANSACTION_METHOD.DELETE) {
                Log.d("DataBaseTransactionQtd", "DELETE - where " + where);
                rowCount = db.delete(queryBuilder.getTableName(), where, null);
            }
        }
        return rowCount;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    public interface InternTransactionQuantityCallBack {
        void onBack(int rowCount);
    }
}

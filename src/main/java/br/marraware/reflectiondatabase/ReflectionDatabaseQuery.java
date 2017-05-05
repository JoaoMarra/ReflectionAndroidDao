package br.marraware.reflectiondatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import br.marraware.reflectiondatabase.model.DaoAbstractModel;

/**
 * Created by joao_gabriel on 05/05/17.
 */

public final class ReflectionDatabaseQuery {

    /**
     * Return the first model with DataBaseQueryBuilder, not async
     * @param modelClass Dao class extending DaoAbstractModel
     * @param queryBuilder Structure to use in select query
     * @return the first model found or null
     */
    public static DaoAbstractModel get(Class<? extends DaoAbstractModel> modelClass, DataBaseQueryBuilder queryBuilder) {
        try {
            Constructor constructor = modelClass.getConstructor();
            DaoAbstractModel model = (DaoAbstractModel) constructor.newInstance();
            SQLiteDatabase db = ReflectionDatabaseManager.db();
            String rawQuery;
            queryBuilder.setTableName(model.tableName(modelClass));
            String query = queryBuilder.getQuery();
            rawQuery = "select * from "+queryBuilder.getTableName()+query;
            Log.d("DataBaseTransaction","GET - "+rawQuery);
            Cursor cursor = db.rawQuery(rawQuery, null);
            if (cursor.moveToFirst()) {
                model.configureWithCursor(cursor);
                return model;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Select using DataBaseQueryBuilder, async method
     * @param modelClass Dao class extending DaoAbstractModel
     * @param queryBuilder Structure to use in select query
     * @param callBack Interface used to compute the data
     */
    public static void getAsync(final Class<? extends DaoAbstractModel> modelClass, DataBaseQueryBuilder queryBuilder, final DataBaseTransactionCallBack callBack) {
        try {
            Constructor constructor = modelClass.getConstructor();
            DaoAbstractModel model = (DaoAbstractModel) constructor.newInstance();
            queryBuilder.setTableName(model.tableName(modelClass));
            DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.GET, new DataBaseTransaction.InternTransactionCallBack() {
                @Override
                public void onBack(Cursor cursor) {
                    if (callBack != null) {
                        ArrayList models = new ArrayList();
                        try {
                            Constructor constructor = modelClass.getConstructor();
                            DaoAbstractModel model;
                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    model = (DaoAbstractModel) constructor.newInstance();
                                    model.configureWithCursor(cursor);
                                    models.add(model);
                                    cursor.moveToNext();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(callBack != null)
                            callBack.onBack(models);
                    }
                }
            });
            transaction.setQueryBuilder(queryBuilder);
            transaction.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.SAVE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(models);
            }

        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }

    public static void deleteAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.DELETE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(models);
            }
        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }

    public static void updateAll(final ArrayList<DaoAbstractModel> models, final DataBaseTransactionCallBack callBack) {
        DataBaseTransaction transaction = new DataBaseTransaction(DataBaseTransaction.TRANSACTION_METHOD.UPDATE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(models);
            }
        });
        transaction.execute((DaoAbstractModel[]) models.toArray());
    }
}

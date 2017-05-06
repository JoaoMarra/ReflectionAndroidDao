package br.marraware.reflectiondatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import br.marraware.reflectiondatabase.model.DaoAbstractModel;
import br.marraware.reflectiondatabase.model.TRANSACTION_METHOD;

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
            DataBaseTransaction transaction = new DataBaseTransaction(TRANSACTION_METHOD.GET, new DataBaseTransaction.InternTransactionCallBack() {
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

    public static void saveAll(final DataBaseTransactionCallBack callBack,final DaoAbstractModel... models) {
        DataBaseTransaction transaction = new DataBaseTransaction(TRANSACTION_METHOD.SAVE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(new ArrayList<DaoAbstractModel>(Arrays.asList(models)));
            }

        });
        transaction.execute(models);
    }

    public static int delete(final Class<? extends DaoAbstractModel> modelClass, DataBaseQueryBuilder queryBuilder) {
        try {
            Constructor constructor = modelClass.getConstructor();
            DaoAbstractModel model = (DaoAbstractModel) constructor.newInstance();
            SQLiteDatabase db = ReflectionDatabaseManager.db();
            String rawQuery;
            queryBuilder.setTableName(model.tableName(modelClass));
            String where = queryBuilder.getWhereString();
            Log.d("DataBaseTransaction", "DELETE - where " + where);
            int rowCount = db.delete(queryBuilder.getTableName(), where, null);

            return rowCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteAsync(final Class<? extends DaoAbstractModel> modelClass, DataBaseQueryBuilder queryBuilder, final DataBaseTransactionQuantityCallBack callBack) {
        try {
            Constructor constructor = modelClass.getConstructor();
            DaoAbstractModel model = (DaoAbstractModel) constructor.newInstance();
            queryBuilder.setTableName(model.tableName(modelClass));
            DataBaseTransactionQuantity transaction = new DataBaseTransactionQuantity(TRANSACTION_METHOD.DELETE, new DataBaseTransactionQuantity.InternTransactionQuantityCallBack() {
                @Override
                public void onBack(int rowCount) {
                    if (callBack != null) {
                        callBack.onBack(rowCount);
                    }
                }
            });
            transaction.setQueryBuilder(queryBuilder);
            transaction.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAll(final DataBaseTransactionCallBack callBack,final DaoAbstractModel... models) {
        DataBaseTransaction transaction = new DataBaseTransaction(TRANSACTION_METHOD.DELETE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(new ArrayList<DaoAbstractModel>(Arrays.asList(models)));
            }
        });
        transaction.execute(models);
    }

    public static void updateAll(final DataBaseTransactionCallBack callBack,final DaoAbstractModel... models) {
        DataBaseTransaction transaction = new DataBaseTransaction(TRANSACTION_METHOD.UPDATE, new DataBaseTransaction.InternTransactionCallBack() {
            @Override
            public void onBack(Cursor cursor) {
                if(callBack != null)
                    callBack.onBack(new ArrayList<DaoAbstractModel>(Arrays.asList(models)));
            }
        });
        transaction.execute(models);
    }

    public static boolean clearTable(final Class<? extends DaoAbstractModel> modelClass) {
        SQLiteDatabase db = ReflectionDatabaseManager.db();

        int rows = db.delete(DaoAbstractModel.tableName(modelClass),"1",null);
        if(rows > 0)
            return true;
        return false;
    }
}

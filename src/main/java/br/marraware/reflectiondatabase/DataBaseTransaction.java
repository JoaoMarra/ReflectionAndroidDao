package br.marraware.reflectiondatabase;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import br.marraware.reflectiondatabase.model.DaoAbstractModel;

import static br.marraware.reflectiondatabase.DataBaseTransaction.TRANSACTION_METHOD.*;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public class DataBaseTransaction extends AsyncTask<DaoAbstractModel,Integer,Cursor> {

    public enum TRANSACTION_METHOD {
        SAVE,DELETE,UPDATE,GET
    }

    private InternTransactionCallBack callBack;
    private String errorMessage;
    private TRANSACTION_METHOD method;
    private DataBaseQueryBuilder queryBuilder;

    public DataBaseTransaction(TRANSACTION_METHOD method,InternTransactionCallBack callBack) {
        this.method = method;
        this.callBack = callBack;
    }

    public DataBaseTransaction(TRANSACTION_METHOD method,InternTransactionCallBack callBack, String errorMessage) {
        this.method = method;
        this.callBack = callBack;
        this.errorMessage = errorMessage;
    }

    public DataBaseQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(DataBaseQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    protected Cursor doInBackground(DaoAbstractModel... daoAbstractModels) {
        if(method == GET) {
            Cursor cursor = null;
            SQLiteDatabase db = DataBaseHelper.db();
            String rawQuery;
            if(queryBuilder != null) {
                String query = queryBuilder.getQuery();
                rawQuery = "select * from "+queryBuilder.getTableName()+query;
                Log.d("DataBaseTransaction","GET - "+rawQuery);
                cursor = db.rawQuery(rawQuery, null);
            }
            return cursor;
        } else {
            for (DaoAbstractModel model : daoAbstractModels) {
                switch (method) {
                    case SAVE:
                        model.save();
                        break;
                    case DELETE:
                        model.delete();
                        break;
                    case UPDATE:
                        model.update();
                        break;
                }
            }
            return new EmptyCursor();
        }
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        if(callBack != null) {
            if(cursor != null)
                callBack.onBack(cursor);
            else
                callBack.onFailure(errorMessage);
        }
    }

    public interface InternTransactionCallBack {

        void onBack(Cursor cursor);

        void onFailure(String errorMessage);
    }

    public class EmptyCursor implements Cursor {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public boolean move(int i) {
            return false;
        }

        @Override
        public boolean moveToPosition(int i) {
            return false;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String s) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int i) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int i) {
            return new byte[0];
        }

        @Override
        public String getString(int i) {
            return null;
        }

        @Override
        public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

        }

        @Override
        public short getShort(int i) {
            return 0;
        }

        @Override
        public int getInt(int i) {
            return 0;
        }

        @Override
        public long getLong(int i) {
            return 0;
        }

        @Override
        public float getFloat(int i) {
            return 0;
        }

        @Override
        public double getDouble(int i) {
            return 0;
        }

        @Override
        public int getType(int i) {
            return 0;
        }

        @Override
        public boolean isNull(int i) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver contentObserver) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver contentObserver) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle bundle) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle bundle) {
            return null;
        }
    }
}

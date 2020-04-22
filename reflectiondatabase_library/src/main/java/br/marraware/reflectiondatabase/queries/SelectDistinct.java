package br.marraware.reflectiondatabase.queries;

/**
 * Created by joao_gabriel on 2020-04-22.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.ColumnModel;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Class used to select rows from table
 *
 * execute return the list of selected models with distinct data
 */
public class SelectDistinct extends QueryType {

    private String[] column;

    private SelectDistinct(String... column) {
        this.column = column;
    }

    public static <T extends DaoModel> SelectDistinctQueryTransaction<T> from(Class<T> T, String... column) {
        return new SelectDistinctQueryTransaction<T>(T, new SelectDistinct(column));
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit, int offset) throws QueryException {

        SQLiteDatabase db = ReflectionDatabaseManager.db();

        String where = whereString();

        StringBuilder columnString = new StringBuilder();
        for(int i=0; i < column.length; i++) {
            columnString.append(column[i]);
            if(i < column.length-1)
                columnString.append(",");
        }

        String rawQuery = "select distinct "+columnString+" from "+DaoModel.tableName(modelClass)+
                (where != null && where.length() > 0?" where"+where:"")+
                (orderBy != null?orderBy:"")+
                (limit != -1?" limit "+limit:"")+
                (offset != -1?" offset "+offset:"");

        Log.d("RelfectionDataBase","SELECT distinct - "+rawQuery);

        return db.rawQuery(rawQuery, null);
    }
}

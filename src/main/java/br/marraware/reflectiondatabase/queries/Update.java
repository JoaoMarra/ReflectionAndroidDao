package br.marraware.reflectiondatabase.queries;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to update rows from table
 *
 * execute return the list of updated models
 */
public class Update extends QueryType {

    protected HashMap<String, Object> values;

    private Update() {
        super();
        values = new HashMap<>();
    }

    public void set(String column, Object value, Class<? extends DaoModel> modelClass) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            values.put(column, value);
        }
    }

    public static <T extends DaoModel> QueryTransaction<T> table(Class<T> T) {
        return new QueryTransaction<T>(T, new Update());
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException {
        try {
            Constructor<T> constructor = modelClass.getConstructor();
            T model = constructor.newInstance();

            SQLiteDatabase db = ReflectionDatabaseManager.db();

            ContentValues cValues = new ContentValues();

            Iterator<String> iterator = values.keySet().iterator();
            String column;
            Object value;

            while (iterator.hasNext()) {
                column = iterator.next();
                value = values.get(column);

                if (value instanceof String) {
                    cValues.put(column, (String) value);
                } else if (value instanceof Integer) {
                    cValues.put(column, (Integer) value);
                } else if (value instanceof Float) {
                    cValues.put(column, (Float) value);
                } else if (value instanceof Double) {
                    cValues.put(column, (Double) value);
                } else if (value instanceof Long) {
                    cValues.put(column, (Long) value);
                } else if (value instanceof Boolean) {
                    if ((Boolean)value)
                        cValues.put(column, 1);
                    else
                        cValues.put(column, 0);
                } else if (value instanceof Date) {
                    cValues.put(column, DaoHelper.dateToString((Date) value));
                }

            }

            String tableName = DaoModel.tableName(modelClass);
            int rows = db.update(tableName, cValues, whereString(), null);

            if(rows > 0)
                return db.rawQuery("select * from " + tableName + " where" + whereString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

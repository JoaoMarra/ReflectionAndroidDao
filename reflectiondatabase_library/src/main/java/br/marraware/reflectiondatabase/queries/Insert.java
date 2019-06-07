package br.marraware.reflectiondatabase.queries;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.InsertIdNotFoundException;
import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.DaoModel;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to insert a row into a table
 *
 * execute return a list of models where the insert model is at first position
 */

public class Insert extends QueryType {

    protected HashMap<String, Object> values;

    private Insert() {
        super();
        values = new HashMap<>();
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit, int offset) throws InsertIdNotFoundException {

        try {
            Constructor<T> constructor = modelClass.getConstructor();
            T model = constructor.newInstance();

            SQLiteDatabase db = ReflectionDatabaseManager.db();

            ContentValues cValues = new ContentValues();

            Iterator<String> iterator = values.keySet().iterator();
            String column;
            Object value;

            String idColumn = model.identifierColumn();
            if(idColumn.compareTo(DaoModel.DEFAULT_ID_COLUMN_NAME) != 0 && !values.containsKey(idColumn)) {
                throw new InsertIdNotFoundException(modelClass, idColumn);
            }
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
            long id = db.insertWithOnConflict(tableName, null, cValues,CONFLICT_REPLACE);

            return db.rawQuery("select * from " + tableName + " where " + model.identifierColumn()+" = "+(values.containsKey(idColumn)?values.get(idColumn):id), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends DaoModel> InsertQueryTransaction<T> into(Class<T> T) {
        return new InsertQueryTransaction<T>(T, new Insert());
    }

    public void set(String column, Object value, Class<? extends DaoModel> modelClass) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            values.put(column, value);
        }
    }

}

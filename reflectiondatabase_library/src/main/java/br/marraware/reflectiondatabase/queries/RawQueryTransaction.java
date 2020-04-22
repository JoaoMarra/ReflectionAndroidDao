package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import java.util.ArrayList;

import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.ColumnModel;
import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 2020-04-22.
 */
public class RawQueryTransaction<T extends DaoModel> extends QueryTransaction {

    public RawQueryTransaction(RawQuery type) {
        super(ColumnModel.class, type);
    }

    @Override
    protected Cursor preExecute() throws QueryException {
        return type.execute(modelClass,null,0,0);
    }

    @Override
    public ArrayList<ColumnModel> execute() throws QueryException {
        Cursor cursor = preExecute();

        ArrayList<ColumnModel> models = null;
        if(cursor != null) {
            try {
                ColumnModel model;
                if (cursor.moveToFirst()) {
                    models = new ArrayList<>();
                    while (!cursor.isAfterLast()) {
                        model = new ColumnModel(modelClass, cursor);
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

    @Override
    public ColumnModel executeForFirst() throws QueryException {
        ArrayList<ColumnModel> models = execute();
        if(models != null && models.size() > 0)
            return models.get(0);
        return null;
    }
}

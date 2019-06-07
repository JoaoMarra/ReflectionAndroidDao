package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.InsertIdNotFoundException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.QueryNode;

/**
 * Created by joao_gabriel on 09/05/17.
 */

/**
 * Class used to insert multiple models on table
 *
 * execute return the list of inserted models
 */

public class InsertMany<V extends DaoModel> extends QueryType {

    protected ArrayList<V> models;

    private InsertMany() {
        super();
        models = new ArrayList<>();
    }

    @Override
    public <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit, int offset) throws InsertIdNotFoundException {

        for(V model : models) {
            model.insert();
        }

        String where = whereString();
        if(where != null && where.length() > 0) {
            SQLiteDatabase db = ReflectionDatabaseManager.db();
            return db.rawQuery("select * from " + DaoModel.tableName(modelClass) + " where " + where, null);
        }

        return null;
    }

    public static <T extends DaoModel> InsertManyQueryTransaction<T> into(Class<T> T) {
        return new InsertManyQueryTransaction<T>(T, new InsertMany<T>());
    }

    public void addModel(V... models) throws ColumnNotFoundException {
        this.models.addAll(Arrays.asList(models));

        ArrayList<QueryNode> nodes = new ArrayList<>();
        for(V model : models) {
            nodes.add(new QueryNode(model.identifierColumn(), model.identifierValue(), WHERE_COMPARATION.EQUAL));
        }
        whereTree(NODE_TREE_COMPARATION.OR,nodes.toArray(new QueryNode[nodes.size()]));
    }

}

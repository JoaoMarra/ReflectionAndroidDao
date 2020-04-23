package br.marraware.reflectiondatabase.queries;

import java.lang.reflect.Method;
import java.util.ArrayList;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.QueryNode;

/**
 * Created by joao_gabriel on 10/05/17.
 */

public abstract class QueryTransactionWhere<T extends DaoModel> extends QueryTransaction<T> {
    public QueryTransactionWhere(Class<T> T, QueryType type) {
        super(T, type);
        if(type instanceof Insert) {
            try {
                Method m = T.getMethod("insertConflictAlgorithm");
                Object o = m.invoke(null);
                type.setConflictType((int)o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(type instanceof Update) {
            try {
                Method m = T.getMethod("updateConflictAlgorithm");
                Object o = m.invoke(null);
                type.setConflictType((int)o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public QueryTransactionWhere<T> where(String column, Object value, WHERE_COMPARATION comparation) throws ColumnNotFoundException {

        type.where(modelClass, column, value, comparation);
        return this;
    }

    public QueryTransactionWhere<T> whereAnd(Object[]... columnValueComparation) throws ColumnNotFoundException {
        String column;
        Object value;
        WHERE_COMPARATION comparation;
        Object[] item;

        ArrayList<QueryNode> nodes = new ArrayList<>();
        for(int i=0; i < columnValueComparation.length; i++) {
            item = columnValueComparation[i];
            if(item != null && item.length == 3) {
                if(item[0] instanceof String) {
                    column = (String) item[0];
                    value = item[1];
                    comparation = (WHERE_COMPARATION) item[2];
                    if(DaoModel.checkColumn(modelClass, column))
                        nodes.add(new QueryNode(column, value, comparation));
                }
            }
        }
        if(nodes.size() > 0) {
            type.whereTree(NODE_TREE_COMPARATION.AND, nodes.toArray(new QueryNode[nodes.size()]));
        }

        return this;
    }

    public QueryTransactionWhere<T> whereOr(Object[]... columnValueComparation) throws ColumnNotFoundException {
        String column;
        Object value;
        WHERE_COMPARATION comparation;
        Object[] item;

        ArrayList<QueryNode> nodes = new ArrayList<>();
        for(int i=0; i < columnValueComparation.length; i++) {
            item = columnValueComparation[i];
            if(item != null && item.length == 3) {
                if(item[0] instanceof String) {
                    column = (String) item[0];
                    value = item[1];
                    comparation = (WHERE_COMPARATION) item[2];
                    if(DaoModel.checkColumn(modelClass, column))
                        nodes.add(new QueryNode(column, value, comparation));
                }
            }
        }
        if(nodes.size() > 0) {
            type.whereTree(NODE_TREE_COMPARATION.OR, nodes.toArray(new QueryNode[nodes.size()]));
        }

        return this;
    }

    public QueryTransactionWhere<T> whereIn(String column, Object... values) throws ColumnNotFoundException {
        type.whereIn(modelClass, column, values);
        return this;
    }

    public QueryTransactionWhere<T> whereNotIn(String column, Object... values) throws ColumnNotFoundException {
        type.whereNotIn(modelClass, column, values);
        return this;
    }

    public QueryTransactionWhere<T> whereBetween(String column, Object value1, Object value2) throws ColumnNotFoundException {
        type.whereBetween(modelClass, column, value1, value2);
        return this;
    }

    public QueryTransactionWhere<T> whereRaw(String query) throws ColumnNotFoundException {
        type.whereRaw(modelClass, query);
        return this;
    }

    public QueryTransactionWhere<T> setConflictType(int conflictType) {
        type.setConflictType(conflictType);
        return this;
    }
}

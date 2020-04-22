package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.helpers.DaoHelper;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.QueryNode;
import br.marraware.reflectiondatabase.utils.QueryNodeBetween;
import br.marraware.reflectiondatabase.utils.QueryNodeRaw;
import br.marraware.reflectiondatabase.utils.QueryNodeTree;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public abstract class QueryType {

    protected ArrayList<QueryNode> nodes;
    protected ArrayList<QueryNodeTree> trees;

    public QueryType() {
        nodes = new ArrayList<>();
        trees = new ArrayList<>();
    }

    public void where(Class<? extends DaoModel> modelClass, String column, Object value, WHERE_COMPARATION comparation) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            nodes.add(new QueryNode(column,value,comparation));
        }
    }

    public void whereIn(Class<? extends DaoModel> modelClass, String column, Object... values) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            StringBuilder inString = new StringBuilder();
            inString.append("(");
            for(int i=0; i < values.length; i++) {
                if(values[i] instanceof String) {
                    inString.append("'"+values[i]+"'");
                } else if(values[i] instanceof Date) {
                    inString.append("'"+ DaoHelper.dateToString((Date) values[i]) +"'");
                } else {
                    inString.append(values[i]);
                }
                if(i < values.length-1)
                    inString.append(",");
            }
            inString.append(")");
            nodes.add(new QueryNode(column,inString,WHERE_COMPARATION.IN));
        }
    }

    public void whereNotIn(Class<? extends DaoModel> modelClass, String column, Object... values) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            StringBuilder inString = new StringBuilder();
            inString.append("(");
            for(int i=0; i < values.length; i++) {
                if(values[i] instanceof String) {
                    inString.append("'"+values[i]+"'");
                } else if(values[i] instanceof Date) {
                    inString.append("'"+ DaoHelper.dateToString((Date) values[i]) +"'");
                } else {
                    inString.append(values[i]);
                }
                if(i < values.length-1)
                    inString.append(",");
            }
            inString.append(")");
            nodes.add(new QueryNode(column,inString,WHERE_COMPARATION.NOT_IN));
        }
    }

    public void whereBetween(Class<? extends DaoModel> modelClass, String column, Object value1, Object value2) throws ColumnNotFoundException {
        if(DaoModel.checkColumn(modelClass, column)) {
            nodes.add(new QueryNodeBetween(column,value1,value2));
        }
    }

    public void whereRaw(Class<? extends DaoModel> modelClass, String query) throws ColumnNotFoundException {
        nodes.add(new QueryNodeRaw(query));
    }

    public void whereTree(NODE_TREE_COMPARATION comparation, QueryNode... nodes) {
        if(nodes.length > 0)
            trees.add(new QueryNodeTree(comparation, nodes));
    }

    public String whereString() {
        StringBuilder builder = new StringBuilder();

        QueryNode node;
        for(int i=0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if(builder.length() > 0)
                builder.append(" AND");
            builder.append(node.toString());
        }
        QueryNodeTree tree;
        for(int i=0; i < trees.size(); i++) {
            tree = trees.get(i);
            if(builder.length() > 0)
                builder.append(" AND");
            builder.append(tree.toString());
        }
        return builder.toString();
    }

    public abstract <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit, int offset) throws QueryException;
}

package br.marraware.reflectiondatabase.queries;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.utils.QueryNode;
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

    public abstract <T extends DaoModel> Cursor execute(Class<T> modelClass, String orderBy, int limit) throws QueryException;
}

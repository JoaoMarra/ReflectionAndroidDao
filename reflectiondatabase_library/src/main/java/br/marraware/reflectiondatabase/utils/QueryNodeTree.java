package br.marraware.reflectiondatabase.utils;

import java.util.ArrayList;
import java.util.Arrays;

import br.marraware.reflectiondatabase.model.NODE_TREE_COMPARATION;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class QueryNodeTree {

    private ArrayList<QueryNode> nodes;
    private NODE_TREE_COMPARATION comparation;

    public QueryNodeTree(NODE_TREE_COMPARATION comparation, QueryNode... nodeArray) {
        nodes = new ArrayList<QueryNode>(Arrays.asList(nodeArray));
        this.comparation = comparation;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(QueryNode node : nodes) {
            if(builder.length() > 0)
                builder.append(" "+comparation);
            builder.append(node.toString());
        }
        return " ("+builder.toString()+")";
    }
}

package br.marraware.reflectiondatabase;

import java.util.ArrayList;

/**
 * Created by joao_gabriel on 03/05/17.
 */

public class DataBaseQueryBuilder {

    public static class QueryTreeNode {
        String column, value;
        QUERY_ITEM_TYPE itemType;

        public QueryTreeNode(String column, String value, QUERY_ITEM_TYPE itemType) {
            this.column = column;
            this.value = value;
            this.itemType = itemType;
        }
    }

    public enum QUERY_ITEM_TYPE {
        EQUAL("="),
        NOTEQUAL("!="),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        CONTAINS("CONTAINS"),
        MORE_THAN(">"),
        MORE_EQUAL(">="),
        LESS_THAN("<"),
        LESS_EQUAL("<=");

        private String name;

        QUERY_ITEM_TYPE(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    public enum QUERY_TREE_CONNECTION {
        AND("AND"),
        OR("OR");

        private String name;

        QUERY_TREE_CONNECTION(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    private abstract class QueryPart{
        public abstract String string();
    }

    private class QueryItem extends QueryPart {
        String column, value;
        QUERY_ITEM_TYPE itemType;

        public QueryItem(String column, String value, QUERY_ITEM_TYPE itemType) {
            this.column = column;
            this.value = value;
            this.itemType = itemType;
        }

        @Override
        public String string() {
            return column+" "+itemType+" "+value;
        }
    }

    private class QueryTree extends QueryPart {
        QueryPart left, right;
        QUERY_TREE_CONNECTION treeConnection;

        public QueryTree(QueryPart left, QueryPart right, QUERY_TREE_CONNECTION treeConnection) {
            this.left = left;
            this.right = right;
            this.treeConnection = treeConnection;
        }

        @Override
        public String string() {
            return left.string()+" "+treeConnection+" "+right.string();
        }
    }

    private String tableName;
    private ArrayList<QueryPart> items;
    private int limit;
    private String orderBy;

    public DataBaseQueryBuilder() {
        tableName = "";
        items = new ArrayList<>();
        limit = -1;
    }

    public DataBaseQueryBuilder where(String column, String value, QUERY_ITEM_TYPE itemType) {
        items.add(new QueryItem(column, value, itemType));
        return this;
    }

    public DataBaseQueryBuilder whereTree(QUERY_TREE_CONNECTION connection,QueryTreeNode... treeNodes) {
        QueryItem left = null, right = null;
        QueryTree tree = null;
        int i=0;
        for(QueryTreeNode node : treeNodes) {
            if(i == 0) {
                left = new QueryItem(node.column,node.value,node.itemType);
                i = 1;
                if(tree != null) {
                    tree = new QueryTree(tree, left, connection);
                    i = 0;
                }
            } else if(i == 1) {
                right = new QueryItem(node.column,node.value,node.itemType);
                tree = new QueryTree(left, right, connection);
                i=0;
            }
        }
        if(tree != null)
            items.add(tree);
        return this;
    }

    public DataBaseQueryBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public DataBaseQueryBuilder setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getQuery() {
        StringBuilder builder = new StringBuilder();
        QueryItem item;
        QueryTree tree;
        if(items.size() > 0) {
            builder.append(" where");
        }
        QueryPart part;
        for(int i = 0; i < items.size(); i++) {
            part = items.get(i);
            if(part instanceof  QueryItem) {
                item = (QueryItem) part;
                builder.append(" "+item.string());
            } else if(part instanceof  QueryTree) {
                tree = (QueryTree) part;
                builder.append(" ("+tree.string()+")");
            }
            if(i < items.size()-1)
                builder.append(" AND");
        }
        if(orderBy != null) {
            builder.append(" order by "+orderBy);
        }
        if(limit != -1) {
            builder.append(" limit "+limit);
        }
        return builder.toString();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}

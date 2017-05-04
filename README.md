# ReflectionDao
SQLite helper for android using reflection

# How to use

Clone and import as a new module dependance.
Each of your DaoModels need to be as the one above:

```
public class DaoModel extends DaoAbstractModel {

    public Integer var1;//need to be object not primitive
    public String var2;

//THESE CONSTRUCTORS ARE IMPORTANT!!!!
    public DaoModel() {
        super();
    }

    public DaoModel(SQLiteCursor cursor) {
        super(cursor);
    }
//
    @Override
    public String identifierColumn() {
        return "var1";//need to be the same name as the identifier variable
    }

    @Override
    public Object identifierValue() {
        return var1;
    }

    @Override
    public String tableName() {
        return "Model";
    }
}
```
On your Application class add to the onCreate() method:
```
ReflectionDatabaseManager.initDataBase(//instance of SQLiteOpenHelper);
```
Finally you can call the methods createTable(SQLiteDatabase) and dropTable(SQLiteDatabase) from your models instance to
create and drop your tables when needed.

# Select Build

You can perform an select query using the DataBaseQueryBuilder class like in this examples below:

Here we want to find the `Person` with `Name` equals `Robert`
```
    DataBaseQueryBuilder builder = new DataBaseQueryBuilder().
    where("Name", "Robert", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL);
    
    new DaoPerson().get(builder, new DataBaseTransactionCallBack() {
                @Override
                public void onBack(ArrayList<DaoAbstractModel> models) {
                    if(models.size() > 0) {
                        models.get(0) <- this is it
                    }
                }
            });
```

You can use these comparation enum:
```
public enum QUERY_ITEM_TYPE {
        EQUAL,
        NOTEQUAL,
        LIKE,
        NOT_LIKE,
        CONTAINS,
        MORE_THAN,
        MORE_EQUAL,
        LESS_THAN,
        LESS_EQUAL
    }
```

Now we want to find every `Person` with `Name` like `Robin` or `Vanessa` with age of 16
```
    DataBaseQueryBuilder builder = new DataBaseQueryBuilder().
    where("Age", "16", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL);
    builder.whereTree(DataBaseQueryBuilder.QUERY_TREE_CONNECTION.OR,
                    new DataBaseQueryBuilder.QueryTreeNode("Name","Robin", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL),
                    new DataBaseQueryBuilder.QueryTreeNode("Name","Vanessa", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL));
    
    new DaoPerson().get(builder, new DataBaseTransactionCallBack() {
                @Override
                public void onBack(ArrayList<DaoAbstractModel> models) {
                    //compute your data
                }
            });
```
Here we used `QueryTreeNode` to create an exclusive where clause with `OR` connection.

# Save, update, delete

Every DaoAbstractModel extended class has methods to `save()`, `update()` and `delete()`. You can use these like in the exemple below:
```
    DaoPerson person = new DaoPerson();
    person.id = 1;
    person.Name = Michelle;
    person.save(); <- save in database
    
    person.Age = 16;
    person.update(); <- update data assuming "id" as identifierColumn
    
    person.delete(); <- delete data from base
```
You can call `saveAll(ArrayList,DataBaseTransactionCallBack)`, `updateAll(ArrayList,DataBaseTransactionCallBack)` and `delete(ArrayList,DataBaseTransactionCallBack)` to modify simutalneous data. Not that this methods (the \`all\` methods) happen async, use `DataBaseTransactionCallBack` interface to know when it\`s finished.

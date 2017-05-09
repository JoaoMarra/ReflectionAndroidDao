# ReflectionDao
SQLite helper for android using reflection

# How to use

Clone and import as a new module dependance.
Each of your DaoModels need to be as the one above:

```
public class DaoModel extends DaoModel {

    @PrimaryKey <- this annotation indicates the primary key, you could not declare any so the DAO will use an default Long
    public Integer var1;//need to be object not primitive
    public String var2;

//THIS CONSTRUCTOR IS IMPORTANT!!!!, YOU MAY NOT INCLUDE IT BUT YOU CAN`T HAVE OTHER CONSTRUCTOR WITHOUT THIS
    public DaoModel() {
        super();
    }
//
}
```
On your Application class add to the onCreate() method:
```
ReflectionDatabaseManager.initDataBase(//instance of SQLiteOpenHelper);
```
Finally on your SQLiteOpenHelper class, you can use these example to create and drop your tables:

```
@Override
    public void onCreate(SQLiteDatabase db) {
        ReflectionDatabaseManager.createTable(DaoModel.class,db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ReflectionDatabaseManager.dropTable(DaoModel.class,db);
        onCreate(db);
    }
```

# Select Build

You can perform get and getAsync methods from `ReflectionDatabaseQuery` class like in this example:

Here we want to find the `Person` with `Name` equals `Robert`
```
    DataBaseQueryBuilder builder = new DataBaseQueryBuilder().
    where("Name", "Robert", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL);
    
    DaoPerson person = (DaoPerson) ReflectionDatabaseQuery.get(DaoPerson.class,builder);
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

Now we want to find every `Person` with `Name` like `Robin` or `Vanessa` with age of 16, for this we\`ll need to use getAsync
```
    DataBaseQueryBuilder builder = new DataBaseQueryBuilder().
    where("Age", "16", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL);
    builder.whereTree(DataBaseQueryBuilder.QUERY_TREE_CONNECTION.OR,
                    new DataBaseQueryBuilder.QueryTreeNode("Name","Robin", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL),
                    new DataBaseQueryBuilder.QueryTreeNode("Name","Vanessa", DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL));
    
    ReflectionDatabaseQuery.getAsync(DaoPerson.class, builder, new DataBaseTransactionCallBack() {
            @Override
            public void onBack(ArrayList<DaoModel> models) {
                //compute your data
            }
        });
```
Here we used `QueryTreeNode` to create an exclusive where clause with `OR` connection.

# Save, update, delete

Every DaoModel extended class has methods to `save()`, `update()` and `delete()`. You can use these like in the exemple below:
```
    DaoPerson person = new DaoPerson();
    person.id = 1;
    person.Name = Michelle;
    person.save(); <- save in database
    
    person.Age = 16;
    person.update(); <- update data assuming "id" as identifierColumn
    
    person.delete(); <- delete data from base
```
You can use `ReflectionDatabaseQuery` to call `saveAll(DataBaseTransactionCallBack, DaoModel...)`, `updateAll(DataBaseTransactionCallBack, DaoModel...)` and `deleteAll(DataBaseTransactionCallBack, DaoModel...)` to modify simutalneous data. Not that these methods happen async, use `DataBaseTransactionCallBack` interface to know when it\`s finished.

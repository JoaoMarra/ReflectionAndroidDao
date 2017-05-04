# ReflectionDao
SQLite helper for android using reflection

# How to use

Clone and import as a new module dependance.
Each of your DaoModels need to be as the one above:

```
public class DaoModel extends DaoAbstractModel {

    public Integer var1;//need to be object not primitive
    public String var2;

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

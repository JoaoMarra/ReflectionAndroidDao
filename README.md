# ReflectionDao
SQLite library for android using reflection. The central ideia is to simplify the creation of DAO classes, and no need for complex configurations.

# How to use

Clone and import as a new module dependance.
Here is some example of DAO classes used on the examples below:

```java
public class Person extends DaoAbstractModel {

    @PrimaryKey
    public Integer rg;//need to be object not primitive
    public String name;
    public Integer age;
    @TableDepedency("rgPerson")
    public List<Address> addresses;
    
    public void addAddress(Address address) {
        if(addresses == null)
            addresses = new ArrayList<Address>();
        address.rgPerson = rg; //not really necessary when save() or update()
        addresses.add(address);
    }
}

public class Address extends DaoAbstractModel {

    public Integer rgPerson;
    public String stringAddress;
    
}
```
`@PrimaryKey` annotation indicates which variable is considered the primaryKey of the table. If you don\`t indicate it, the library will create an default Long id that can be accessed with the method `identifierValue()` and `identifierColumn()` to access the column name.
`@TableDepedency("foreignKey")` annotation indicates association between tables (1-1 or 1-n). In the example, Person has `n` addresses, and the foreignKey its `rgPerson` on Address class. Wherever a Person is saved, updated or deleted every Address with `rgPerson` equals Person identifier(`rg`) will be saved, updated and deleted.

On your Application class add to the onCreate() method:
```java
ReflectionDatabaseManager.initDataBase(//instance of SQLiteOpenHelper);
```
Finally on your SQLiteOpenHelper class, you can use these example to create and drop your tables:

```java
@Override
    public void onCreate(SQLiteDatabase db) {
        ReflectionDatabaseManager.createTable(Person.class,db);
        ReflectionDatabaseManager.createTable(Address.class,db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ReflectionDatabaseManager.dropTable(Person.class,db);
        ReflectionDatabaseManager.dropTable(Address.class,db);
        onCreate(db);
    }
```

# Usage examples

Here some code using the lib:
```java
    Person person1 = new Person();
    person1.rg = 1;
    person1.name = Paulo;
    person1.age = 16;
    person1.save(); //creates or update the Person with rg = 1
    
    Address address1 = new Address();
    address1.stringAddress = "somewhere";
    person1.addAddress(address1);
    person1.update(); //update person1 saving the address
   
    ...
    
    Person person2 = (Person) ReflectionDatabaseQuery.get(Person.class, new DataBaseQueryBuilder().where("rg",1, DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL));
    System.out.println(person2.addresses.get(0).stringAddress); //will print "somewhere"
    
    ...
    
    ReflectionDatabaseQuery.getAsync(Address.class, new DataBaseQueryBuilder().where("rgPerson",1, DataBaseQueryBuilder.QUERY_ITEM_TYPE.EQUAL), new DataBaseTransactionCallBack() {
            @Override
            public void onBack(ArrayList<DaoAbstractModel> models) {
                ArrayList<Address> addresses = (ArrayList<Address>) models;
                System.out.println(addresses.get(0).stringAddress); //will print "somewhere"
            }
        });
        
    ...
    
    ReflectionDatabaseQuery.clearTable(Person.class);
    System.out.println("Count - "+address1.rowCount()); //will print "Count - 0"
```

# Important features

You can manipulate your data using the DaoClasse itself or using `ReflectionDatabaseQuery` class where you can `get`, `getAll`, `getAsync`, `saveAll`, `delete`, `deleteAsync`, `deleteAll`, `updateAll` and `clearTable`. This you cant learn how to use on Repository Wiki.

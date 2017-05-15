# ReflectionDao
SQLite library for android using reflection. The central ideia is to simplify the creation of DAO classes, and **no need for complex configurations**.

# Add to project

First add on you project\`s build.gradle :

```
allprojects {
    repositories {
        jcenter()
    }
}
```

Now add on your app module\`s build.gradle dependecy:
```
compile 'br.marraware:reflectiondatabase_library:2.3'
```

# How to use

Here is some example of DAO classes used on the examples below:

```java
public class Person extends DaoModel {

    @PrimaryKey
    public Integer rg;//need to be object not primitive
    public String name;
    public Integer age;
    @TableDepedency("rgPerson")
    public List<Address> addresses;
    public Double currency;
    
    public void addAddress(Address address) {
        if(addresses == null)
            addresses = new ArrayList<Address>();
        address.rgPerson = rg; //not really necessary when save() or update()
        addresses.add(address);
    }
}

public class Address extends DaoModel {

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

You can use the lib directly from the model class or using Query Classes. Here is some examples:

```java
    Person person1 = new Person();
    person1.rg = 1;
    person1.name = Paulo;
    person1.age = 16;
    person1.insert(); //creates or update the Person with rg = 1
    
    Address address1 = new Address();
    address1.stringAddress = "somewhere";
    person1.addAddress(address1);
    person1.update(); //update person1 saving the address
   
    ...
    
    person1.delete(); //delete the person1 and every address with rgPerson = person1.rg
```
Here is how you can `Insert`, `InsertMany`, `Select`, `Update` and `Delete` from tables not models:

```java
    Person person1 = Insert.into(Person.class)
                           .set("rg", 1)
                           .set("name", "Paulo")
                           .set("age", "Paulo")
                           .executeForFirst();
                    
    ArrayList<Address> addresses = Select.from(Address.class)
                                         .where("rgPerson", 1, WHERE_COMPARATION.EQUAL)
                                         .orderBy(DaoModel.DEFAULT_ID_COLUMN_NAME, ORDER_BY.ASCENDING)
                                         .execute();
                                    
    ...
    
    Update.table(Person.class)
          .set("currency",0.0)
          .where("age", 20, WHERE_COMPARATION.LESS_THAN)
          .execute();
          
    ...
   
    Delete.from(Person.class)
          .where("age", 10, WHERE_COMPARATION.MORE_EQUAL)
          .executeAsync(new AsyncQueryCallback<Person>() {
                        @Override
                        public void onBack(List<Person> models) {
                            //delete completed
                        }
                    });
                    
     ...
     
     //insert many models at once
     InsertMany.into(Person.class)
               .addModel(person1, person2, person3)
               .execute();
```

Every action is type safe and can throw `ColumnNotFoundException` or `InsertIdNotFoundException` when some column is not found on DaoModel, or when you try to insert some model with identification = null.

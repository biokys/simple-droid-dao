simple-droid-dao
================

Simple DB access library for Android

It is developed for extremely easy and fast access to Sqlite DB system on Android.

Root folder contains
--------------------

- simple-droid-dao - main library project
- simple-droid-dao-test - supporting empty android project for instrumentation test project
- simple-droid-dao-test - instrumentation android project

Fast tutorial
----------------
This tutorial will show you how easy you can access db with your code. 

If you want SimpleDroidDao library to manage creationDate and modifyDate for you for every record in DB, than you have to extends from BaseDateModel class


```java

// telling SDD, that this class can be serialized to DB
@GenericModel.Entity
// set table name for this class
@GenericModel.TableName(name = "user")
// LONG or UUID primary key
@GenericModel.IdType(type = GenericModel.IdTypeEnum.LONG)
public class User extends BaseDateModel<User> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @DataType(type = DataTypeEnum.INTEGER)
    @Index
    public int age;

    @DataType(type = DataTypeEnum.DATE)
    public Date birthday;

    // you can simply store enums to DB
    @DataType(type = DataTypeEnum.ENUM)
    public CountryEnum country;


}
```
Helper enum for User class
```java
public enum CountryEnum {

    CZECH,
    UK,
    USA

}
```

Class with UUID autogenerated primary key 
```java

@GenericModel.Entity
@GenericModel.TableName(name = "customer")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.UUID)
public class Customer extends BaseDateModel<Customer> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;
}
```

Model class when you can see how to use foreign keys 
```java

@GenericModel.Entity
@GenericModel.TableName(name = "userFK")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.LONG)
public class UserFK extends BaseDateModel<UserFK> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @ForeignKey(attributeClass = Customer.class, deleteOnCascade = true)
    public Id customerId;


}

```

```java

public class Application extends android.app.Application {
        
    public static final int NEW_DATABASE_VERSION = 5;

    public static final String DB_NAME = "test.db";

    @Override
    public void onCreate() {
        super.onCreate();

        // at first, you must register all model classes you want to serialize
        SimpleDroidDao.registerModelClass(User.class);
        SimpleDroidDao.registerModelClass(Customer.class);

        // this method initialize SDD system
        // if you want to manage db upgrade version, just implement IUpgradeHandler
        // if you dont, leave param null, and SDD will erase db after every version change
        SimpleDroidDao.initialize(this, DB_NAME, NEW_DATABASE_VERSION, new SimpleDroidDao.IUpgradeHandler() {
            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

                switch (oldVersion) {

                    case 2:

                        // some DB modifications, ex. Alter table, add index, etc.                    
                    case 3:

                        // some DB modifications, ex. Alter table, add index, etc.                        
                    case 4:

                        // some DB modifications, ex. Alter table, add index, etc.
                        break;
                }
            }
        });
    }
}

```

Following code can be used everywhere in your app. No need for getting SDD context
```java

// somewhere in your application

// create new user instance
User user = new User();

// setup properties
user.age = 10;
user.birthday = new Date();
user.country = CountryEnum.CZECH;
user.name = "Tony";

// save to db
user.save();

// find user by existing id
User existingUser = User.findObjectById(User.class, user.id);

// get all objects from db
List<User> list = User.getAllObjects(User.class);

// get all objects from table
// returns hashmap, where key is object id and value is object itself
Map<Id,User> userMap = User.getAllObjectsAsMap(User.class);


// delete user from db
existingUser.delete();


// create Customer instance
Customer customer = new Customer();
customer.name = "Lenka";

// save customer to DB
customer.save();

// create UserFK instance
UserFK user = new UserFK();
user.name = "Peter";

// set binding
user.customerId = customer.id;

// save user 
user.save();

// delete customer and also user - because of deleteOnCascade attribute
customer.delete();

// using of transactions
try {

    GenericModel.beginTx();

    Customer customer = new Customer();
    customer.name = "Ozzy";
    customer.save();

    UserFK user = new UserFK();
    user.name = "Alice";
    user.customerId = customer.id;
    user.save();

    UserFK user2 = new UserFK();
    user2.name = "Bob";
    user2.customerId = customer.id;

    user2.save();

    GenericModel.setTxSuccesfull();
} finally {

    GenericModel.endTx();
}
```


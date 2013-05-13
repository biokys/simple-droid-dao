package eu.janmuller.android.test.dao;

import android.test.ActivityInstrumentationTestCase2;
import eu.janmuller.android.dao.api.GenericModel;
import eu.janmuller.android.dao.api.SimpleDroidDao;
import eu.janmuller.android.dao.exceptions.SimpleDroidDaoException;

import java.util.Date;
import java.util.List;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class eu.janmuller.android.test.dao.TestActivityTest \
 * eu.janmuller.android.test.dao.tests/android.test.InstrumentationTestRunner
 */
public class TestActivityTest extends ActivityInstrumentationTestCase2<TestActivity> {

    public static final String DB_NAME = "SDD.db";


    @Override
    protected void setUp() throws Exception {

        getActivity().deleteDatabase(DB_NAME);

        SimpleDroidDao.registerModelClass(Customer.class);
        SimpleDroidDao.registerModelClass(UserFK.class);
        SimpleDroidDao.registerModelClass(User.class);
        SimpleDroidDao.registerModelClass(UniqueEmail.class);
        SimpleDroidDao.registerModelClass(IndexedModel.class);

        SimpleDroidDao.initialize(getActivity(), DB_NAME, 1, null);

    }


    public TestActivityTest() {

        super("eu.janmuller.android.test.dao", TestActivity.class);
    }

    public void testSaveAndDelete() throws Exception {

        User user = new User();
        user.age = 10;
        user.birthday = new Date();
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        User existingUser = User.findObjectById(User.class, user.id);
        assertNotNull(existingUser);

        existingUser.delete();

        existingUser = User.findObjectById(User.class, user.id);
        assertNull(existingUser);

    }

    public void testSaveRetrieveAndCount() throws Exception {

        User user = new User();
        user.age = 10;
        user.birthday = new Date();
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        user = new User();
        user.age = 10;
        user.birthday = new Date();
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        user = new User();
        user.age = 10;
        user.birthday = new Date();
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        List<User> list = User.getAllObjects(User.class);
        assertEquals(list.size(), 3);

    }

    public void testSaveEditSave() throws Exception {

        Date d1 = new Date();
        Date d2 = new Date(120, 3, 2);

        User user = new User();
        user.age = 10;
        user.birthday = d1;
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        User newUser = User.findObjectById(User.class, user.id);

        assertNotNull(newUser);

        newUser.age = 20;
        newUser.birthday = d2;
        newUser.country = CountryEnum.USA;
        newUser.name = "Alex";
        newUser.save();

        assertEquals(user.id.toString(), newUser.id.toString());
        assertEquals(1, User.getAllObjects(User.class).size());

    }

    public void testCreateModifiyDate() throws Exception {

        Date d = new Date(120, 3, 2);

        User user = new User();
        user.age = 10;
        user.birthday = d;
        user.country = CountryEnum.CZECH;
        user.name = "Honza";
        user.save();

        try {

            Thread.sleep(20);
        } catch (Exception e) {
        }

        User existingUser = User.findObjectById(User.class, user.id);

        assertNotNull(existingUser);
        assertNotNull(existingUser.creationDate);
        assertNotNull(existingUser.modifiedDate);
        assertEquals(existingUser.creationDate, existingUser.modifiedDate);


        user.name = "Alex";

        user.save();


        existingUser = User.findObjectById(User.class, user.id);

        assertNotNull(existingUser);
        assertNotNull(existingUser.creationDate);
        assertNotNull(existingUser.modifiedDate);
        assertNotSame(existingUser.creationDate, existingUser.modifiedDate);
    }

    public void testFK() throws Exception {

        Customer customer = new Customer();
        customer.name = "Lenka";
        customer.save();

        UserFK user = new UserFK();
        user.name = "Honza";
        user.customerId = customer.id;
        user.save();

        UserFK user2 = new UserFK();
        user2.name = "Petr";
        user2.customerId = customer.id;
        user2.save();

        UserFK newUser = UserFK.findObjectById(UserFK.class, user2.id);

        assertNotNull(newUser);
        assertEquals(newUser.customerId, customer.id);

        assertEquals(2, UserFK.getAllObjects(UserFK.class).size());
        assertEquals(1, Customer.getAllObjects(Customer.class).size());

        customer.delete();

        assertEquals(0, UserFK.getAllObjects(UserFK.class).size());
        assertEquals(0, Customer.getAllObjects(User.class).size());

    }

    public void testSetGetFields() throws Exception {

        User user = new User();

        user.age = 10;
        user.birthday = new Date();
        user.country = CountryEnum.CZECH;
        user.name = "XXX";
        user.save();

        Date cd = user.creationDate;
        Date md = user.modifiedDate;

        assertNotNull(cd);
        assertNotNull(md);

        User newUser = User.findObjectById(User.class, user.id);

        assertNotNull(newUser);

        assertEquals(user.country, newUser.country);
        assertEquals(user.age, newUser.age);
        assertEquals(user.birthday, newUser.birthday);
        assertEquals(user.name, newUser.name);
        assertEquals(cd, newUser.creationDate);
        assertEquals(md, newUser.modifiedDate);

        newUser.country = CountryEnum.UK;
        newUser.save();

        User newUser2 = User.findObjectById(User.class, newUser.id);

        assertNotNull(newUser2);

        assertEquals(CountryEnum.UK, newUser2.country);

    }

    public void testSuccessTX() throws Exception {

        try {

            GenericModel.beginTx();

            Customer customer = new Customer();
            customer.name = "C";
            customer.save();

            UserFK user = new UserFK();
            user.name = "A";
            user.customerId = customer.id;
            user.save();

            UserFK user2 = new UserFK();
            user2.name = "A";
            user2.customerId = customer.id;

            user2.save();

            GenericModel.setTxSuccesfull();
        } catch (Exception e) {

        }

        GenericModel.endTx();

        assertEquals(2, UserFK.getAllObjects(UserFK.class).size());
        assertEquals(1, Customer.getAllObjects(Customer.class).size());

    }

    public void testFailTX() throws Exception {

        try {

            GenericModel.beginTx();

            Customer customer = new Customer();
            customer.name = "C";
            customer.save();

            UserFK user = new UserFK();
            user.name = "A";
            user.customerId = customer.id;
            user.save();

            UserFK user2 = new UserFK();
            user2.name = "A";

            user2.save();

            GenericModel.setTxSuccesfull();
        } catch (Exception e) {

        }

        GenericModel.endTx();

        assertEquals(0, UserFK.getAllObjects(UserFK.class).size());
        assertEquals(0, Customer.getAllObjects(Customer.class).size());

    }

    public void testUnique() throws Exception {

        boolean exception = false;

        try {

            UniqueEmail uniqueEmail = new UniqueEmail();
            uniqueEmail.email = "a";
            uniqueEmail.save();

            uniqueEmail = new UniqueEmail();
            uniqueEmail.email = "ab";
            uniqueEmail.save();

            uniqueEmail = new UniqueEmail();
            uniqueEmail.email = "a";
            uniqueEmail.save();
        } catch (SimpleDroidDaoException sdde) {

            exception = true;
        }

        assertEquals(exception, true);

    }

    public void testNotnull() throws Exception {

        boolean exception = false;

        try {

            UniqueEmail uniqueEmail = new UniqueEmail();
            uniqueEmail.save();
        } catch (SimpleDroidDaoException sdde) {

            exception = true;
        }

        assertEquals(exception, true);

    }

    public void testIndexes() throws Exception {

        IndexedModel indexedModel = new IndexedModel();
        indexedModel.save();
    }


}

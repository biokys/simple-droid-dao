package eu.janmuller.android.dao;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: biokys
 * Date: 26.3.12
 * Time: 10:24
 */
public class DaoService {

    private static Map<Class, DaoModelRelation> relationList = new LinkedHashMap<Class, DaoModelRelation>();

    private static DatabaseAdapter databaseAdapter;


    private static class DaoModelRelation<T extends IBaseDao, U extends BaseModel> {

        public Class<T> dao;

        public Class<U> model;

        public T instance;
    }

    public static List<IBaseDao> getAllDaoInstances() {

        List<IBaseDao> list = new ArrayList<IBaseDao>();

        for (Class next : relationList.keySet()) {

            list.add(getInstance(next));

            //list.add(relationList.get(next).instance);
        }

        return list;
    }

    private static <T extends IBaseDao> DaoModelRelation getDaoModelRelationItem(Class<T> daoClass) {

        // podivam se do hashmapy, zda obsahuje objekt pro zadanou tridu
        DaoModelRelation daoModelRelation = relationList.get(daoClass);

        // pokud instance relacniho objektu neni nalezena v seznamu, pak vyhodime vyjimku
        if (daoModelRelation == null) {

            throw new IllegalStateException("Dao class wasn't registered, put registerDaoClass method to Application class");
        }

        return daoModelRelation;
    }

    /**
     * Metoda vraci singleton instanci DAO objektu
     *
     * @param daoClass Trida, ktera je oddedena od
     * @return null, pokud se nepovede instanciovat objekt
     */
    public static synchronized <T extends IBaseDao> T getInstance(Class<T> daoClass) {

        DaoModelRelation daoModelRelation = getDaoModelRelationItem(daoClass);

        T instance = (T) daoModelRelation.instance;

        // pokud ano
        if (instance != null) {

            // pak vratim instanci
            return instance;
        } else {

            // pokud ne
            try {

                // vytvorim novou instanci
                instance = daoClass.newInstance();
                daoModelRelation.instance = instance;

                // vlozim ji do hashmapy
                relationList.put(daoClass, daoModelRelation);

                // vratim instanci
                return instance;
            } catch (IllegalAccessException e) {

                Log.e(DaoConstants.LOG_TAG, "Can not create instance of DaoService Object", e);
            } catch (InstantiationException e) {

                Log.e(DaoConstants.LOG_TAG, "Can not create instance of DaoService Object", e);
            }
        }

        // pokud se nepodari vytvorit instanci vracim vyhodim vyjimku
        throw new RuntimeException("dao class has never been registered");
    }

    /**
     * Metoda vraci asociovany model class ke specifikovanemu dao classu
     */
    public static <T extends IBaseDao> Class getModelByDao(Class<T> daoClass) {

        return getDaoModelRelationItem(daoClass).model;
    }

    /**
     * Metoda vraci asociovany dao class ke specifikovanemu model classu
     */
    public static <T extends BaseModel> Class getDaoByModel(Class<T> modelClass) {

        for (Map.Entry<Class, DaoModelRelation> entry : relationList.entrySet()) {

            Class<T> clazz = entry.getValue().model;

            if (clazz.equals(modelClass)) {

                return entry.getValue().dao;
            }
        }

        throw new IllegalStateException("dao class has never been registered");
    }

    /**
     * Zaregisruje Dao tridu.
     * Musi by volana jako prvni metoda idealne v onCreate metode tridy odddedene od app.Application
     */
    public static <T, U> void registerDaoClass(Class<T> daoClass, Class<U> modelClass) {

        DaoModelRelation daoModelRelation = new DaoModelRelation();
        daoModelRelation.dao = daoClass;
        daoModelRelation.model = modelClass;
        relationList.put(daoClass, daoModelRelation);
        Log.i(DaoConstants.LOG_TAG, "class " + daoClass.getName() + " registered");
    }


    public static void beginTx() {

        databaseAdapter.getOpenedDatabase().beginTransaction();
    }

    public static void endTx() {

        databaseAdapter.getOpenedDatabase().endTransaction();
    }

    public static void setTxSuccesfull() {

        databaseAdapter.getOpenedDatabase().setTransactionSuccessful();
    }


    /**
     * Metoda pomoci ktere inicializuje Dao api
     * Musi by volana az po register metode idealne v onCreate metode tridy odddedene od app.Application
     *
     * @param context         Androidi context
     * @param databaseName    Jmeno databaze
     * @param databaseVersion Verze databaze
     * @param upgradeHandler  Handler na ktery muzeme navazat udalost spojenou s upgradem databaze
     */
    public static void initializeDao(Context context, String databaseName, int databaseVersion, DatabaseAdapter.IUpgradeHandler upgradeHandler) {

        databaseAdapter = new DatabaseAdapter(context, databaseName, databaseVersion);
        databaseAdapter.setOnUpgradeHandler(upgradeHandler);

        Log.i(DaoConstants.LOG_TAG, "database " + databaseName + " v" + databaseVersion + " initialized");
    }

    /**
     * Metoda pomoci ktere inicializuje Dao api
     * Musi by volana az po register metode idealne v onCreate metode tridy odddedene od app.Application
     * Pri upgradu dojde pokazde ke zruseni cele databaze a jejimu novemu vytvoreni. Pokud je potreba ovlivnit upgrade mechanismus,
     * pouzijte pretizenou metodu
     *
     * @see DaoService#initializeDao(android.content.Context, String, int, eu.janmuller.android.dao.DatabaseAdapter.IUpgradeHandler)
     * @param context         Androidi context
     * @param databaseName    Jmeno databaze
     * @param databaseVersion Verze databaze
     */
    public static void initializeDao(Context context, String databaseName, int databaseVersion) {

        initializeDao(context, databaseName, databaseVersion, null);
    }

    /**
     * For unit testing purpose mainly
     *
     * @return
     */
    public static DatabaseAdapter getDatabaseAdapter() {

        return databaseAdapter;
    }

}

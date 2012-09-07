package eu.janmuller.android.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.janmuller.android.dao.exceptions.DaoConstraintException;
import eu.janmuller.android.dao.exceptions.DaoException;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Skeletalni implementace rozhrani <p>IBaseDao</p>
 * @param <T> Trida reprezentujici datovou entitu, ktera dedi od <p>BaseModel</p>
 *
 * @see eu.janmuller.android.dao.BaseModel
 * @see eu.janmuller.android.dao.IBaseDao
 */
public abstract class AbstractBaseDao<T extends AbstractModel> implements IBaseDao<T> {

    T t;

    /**
     * Zakladni atributy, ktere ma kazdy objekt, ktery dedi od BaseModel
     */
    private static final String[] basicAttributes = new String[]{
            KEY_ID,
            KEY_CREATION_DATE,
            KEY_MODIFY_DATE,
    };


    /**
     * Vraci instanci objektu pro praci s DB
     */
    protected SQLiteDatabase getSQLiteDatabase() {

        return DaoService.getDatabaseAdapter().getOpenedDatabase();
    }

    /**
     * Slouzi k mapovani objektu na DB objekty
     * Podtridy vraceji mapovani
     */
    abstract protected void getObject2DbMapping(final ContentValues contentValues, T object);

    /**
     * Metoda vraci s podtridy nazev tabulky se kterou je trida spojena
     *
     * @return jmeno tabulky
     */
    abstract protected String getTableName();

    /**
     * Slouzi pro mapovani z cursoru do objektu
     */
    abstract protected T getCursor2ObjectMapping(Cursor c);

    /**
     * Metoda, ktera vraci pole atributu - pouze custom
     * Zakladni atributy (id, create, modify) jsou automaticky pridany v API
     * Zalezi na poradi!
     *
     * @return
     */
    abstract protected String[] getAttributes();

    /**
     * Vraci sql prikaz pro create tabulky
     * Tuto metodu implementuji podtridy. V tele techto implementaci se nachazi kod, ktery vytvari
     * sqlko, ktere vytvari tabulku
     * Pro lepsi praci je do metody dana instance create table builderu
     */
    abstract protected void getCreateTableSql(final CreateTableSqlBuilder builder);

    private String getCreateTableSql() {

        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        CreateTableSqlBuilder cb;
        if (clazz.getGenericSuperclass() == BaseModel.class) {

            cb = getCreateTableSqlBuilderInstance(CreateTableSqlBuilder.IDTypes.LONG_AUTOINCREMENT);
        } else if (clazz.getGenericSuperclass() == BaseUuidModel.class) {

            cb = getCreateTableSqlBuilderInstance(CreateTableSqlBuilder.IDTypes.UUID);
        } else {

            throw new IllegalStateException("no id type selected");
        }
        getCreateTableSql(cb);
        return cb.create();
    }

    /**
     * Metoda nejprve zpracuje implicitni mapovani (id, created, modified)
     * a pote nacte mapovani z klientske tridy
     */
    protected ContentValues getObject2DbMapping(T object) {

        ContentValues contentValues = object2DbMappingHelper(object);

        getObject2DbMapping(contentValues, object);

        return contentValues;
    }

    /**
     * Metoda ve ktere probiha mapovani z objektu na objekt ContentValues,
     * se kterym pracuje SQLite DB Adapter
     * V teto metode se predpripravi objekt ContentValues tim ze se vygeneruje ID,
     * cas vytvoreni a cas modifikovani v zavislosti na tom, zda se jedna o novy objekt, nebo
     * jiz existuji
     * Metoda slouzi jen jako pomocna
     */
    private ContentValues object2DbMappingHelper(T object) {

        ContentValues initialValues = new ContentValues();

        long nowTimestamp = new Date().getTime();

        // pokud objekt neobsahuje id, pak je novy a
        //potrebujeme spravne nastavit datum + vygenerovat ID
        if (object.isNew()) {

            initialValues.put(KEY_CREATION_DATE, nowTimestamp);

            Object o = object.getNewId();
            if (o instanceof String) {

                initialValues.put(KEY_ID, (String)o);
            }
        }

        initialValues.put(KEY_MODIFY_DATE, nowTimestamp);

        return initialValues;
    }

    /**
     * Metoda mapuje cursor na objekt
     * Nejprve vlozi mapovani z klientske tridy a pote pripoji implicitni
     */
    private T getCursor2ObjectMappingHelper(Cursor c) {

        T object = getCursor2ObjectMapping(c);

        if (object instanceof BaseModel) {

            object.setId(c.getLong(c.getColumnIndex(IBaseDao.KEY_ID)));
        } else if (object instanceof BaseUuidModel) {

            object.setId(c.getString(c.getColumnIndex(IBaseDao.KEY_ID)));
        } else {

            throw new IllegalStateException("you used bad generic!");
        }

        object.created = new Date(c.getLong(c.getColumnIndex(KEY_CREATION_DATE)));
        object.modified = new Date(c.getLong(c.getColumnIndex(KEY_MODIFY_DATE)));

        return object;
    }

    @Override
    public T retrieveById(Object id) {

        String[] attributes = prepareAttributes();

        Cursor cursor = getSQLiteDatabase().query(getTableNameWithCheck(),
                attributes
                , KEY_ID + "=?",
                new String[]{id.toString()}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            T object = getCursor2ObjectMappingHelper(cursor);

            cursor.close();

            return object;
        }

        return null;
    }

    @Override
    public Object insertOrUpdate(T object) {

        // namapuji objekt na db objekt
        ContentValues cv = getObject2DbMapping(object);

        boolean isUpdate = false;

        try {
            // pokud nema objekt vyplnene id, pak vytvarime novy zaznam do DB
            if (object.isNew()) {

                // insertujem do db
                long id = getSQLiteDatabase().insertOrThrow(getTableNameWithCheck(), null, cv);

                // pokud nedoslo k chybe
                if (id != -1) {

                    // vratime vygenerovane id
                    return cv.get(KEY_ID);
                } else {

                    // jinak vyhodime runtime exception
                    throw new RuntimeException("insert failed");
                }

            } else {

                // pokud jiz existuje id, pak provedem update
                long updatedID = getSQLiteDatabase().update(getTableNameWithCheck(), cv, KEY_ID + "='" + object.getId() + "'", null);

                isUpdate = true;

                // pokud update probehl v poradku
                if (updatedID > 0) {

                    // vratime vygenerovane id
                    return object.getId();
                } else {

                    // jinak vyhodime runtime exception
                    throw new RuntimeException("update failed for object id " + object.getId());
                }
            }
        } catch (SQLiteConstraintException sce) {


            throw new DaoConstraintException(isUpdate ? DaoConstraintException.ConstraintsExceptionType.UPDATE :
                    DaoConstraintException.ConstraintsExceptionType.INSERT, sce);
        }
    }

    @Override
    public Cursor retrieveAllInCursor() {

        String selectQuery = "SELECT *, rowid _id FROM " + getTableNameWithCheck();

        return getSQLiteDatabase().rawQuery(selectQuery, null);
    }

    @Override
    public List<T> retrieveAll() {

        Cursor cursor = retrieveAllInCursor();

        return getListFromCursor(cursor);
    }

    /**
     * Metoda pro ziskani seznamu objektu.
     * Metoda interne vola metodu <p>retrieveByQueryInCursor</p>
     *
     * @see eu.janmuller.android.dao.AbstractBaseDao#retrieveByQueryInCursor
     *
     * @param whereClause klasicka SQL where klauzule
     * @return List objektu
     */
    @Override
    public List<T> retrieveByQuery(String whereClause) {

        Cursor cursor = retrieveByQueryInCursor(whereClause);

        return getListFromCursor(cursor);
    }

    @Override
    public Cursor retrieveByQueryInCursor(String whereClause) {

        String selectQuery = "SELECT *, rowid _id FROM " + getTableNameWithCheck() + " WHERE " + whereClause;

        return getSQLiteDatabase().rawQuery(selectQuery, null);
    }

    @Override
    public Map<Object, T> getAllObjectsAsMap() {

        Map<Object, T> map = new HashMap<Object, T>();
        List<T> objects = retrieveAll();
        for (T t : objects) {

            map.put(t.getId(), t);
        }

        return map;
    }

    @Override
    public void delete(T object) {

        try {
            getSQLiteDatabase().delete(getTableNameWithCheck(), KEY_ID + " = ?",
                    new String[]{object.getId().toString()});

        } catch (SQLiteConstraintException sce) {

            throw new DaoConstraintException(DaoConstraintException.ConstraintsExceptionType.DELETE, sce);
        }
    }

    @Override
    public void deleteAll() {

        getSQLiteDatabase().delete(getTableNameWithCheck(), null, null);
    }

    @Override
    public void deleteByQuery(String whereClause) {

        getSQLiteDatabase().delete(getTableNameWithCheck(), whereClause, null);
    }

    @Override
    public void dropTable(SQLiteDatabase db) {

        db.execSQL("drop table if exists " + getTableNameWithCheck());
    }

    @Override
    public void createTable(SQLiteDatabase db) {

        db.execSQL(getCreateTableSql());
    }

    /**
     * Pomocna metoda pro vraceni instance builderu pro jednodussi vytvareni CREATE TABLE SQL
     * Builder je jiz nastaven aby vyvoril tabulka se jmenem tabulky podle getTableName
     *
     * @return instance createTableSqlBuilderu
     */
    private CreateTableSqlBuilder getCreateTableSqlBuilderInstance(CreateTableSqlBuilder.IDTypes types) {

        return new CreateTableSqlBuilder(getTableNameWithCheck(), types);
    }

    /**
     * Metoda, ktera spoji zakladni atributy (id, create, modify) s custom atributy
     *
     * @return
     */
    private String[] prepareAttributes() {

        String[] attributes = new String[basicAttributes.length + getAttributes().length];

        String[] customAttributes = getAttributes();

        System.arraycopy(basicAttributes, 0, attributes, 0, basicAttributes.length);

        System.arraycopy(customAttributes, 0, attributes, basicAttributes.length, customAttributes.length);

        return attributes;
    }

    /**
     * Vraci seznam objektu, ktery dostaneme v cursoru
     */
    protected List<T> getListFromCursor(Cursor cursor) {

        List<T> list = new ArrayList<T>();

        // pokud je cursor nullovy, pak vratime prazdny seznam
        if (cursor == null) {

            return list;
        }

        // pokud v kurzoru je alespon jeden zaznam
        if (cursor.moveToFirst()) {

            // prochazej pres vsechny zaznamy v cursoru dokud muzes
            do {

                // vytvor objekt
                T object = getCursor2ObjectMappingHelper(cursor);

                // a pridej ho do seznamu
                list.add(object);

            } while (cursor.moveToNext());
        }

        // uzavreme cursor
        cursor.close();

        return list;
    }

    /**
     * Interni Api metoda pro kontrolu zda byl nastaven nazev tabulky
     * Pokud ne, pak je vyhozena vyjimka
     *
     * @return
     */
    private String getTableNameWithCheck() throws DaoException {

        String name = getTableName();

        // pokud metoda getTableName nevratila zadny nazev, pak vyhodime vyjimku
        if (name == null || name.length() == 0) {

            String errorMessage = "you didnt implement getTableName method properly";
            Log.e(DaoConstants.LOG_TAG, errorMessage);
            throw new DaoException(errorMessage);
        }

        return getTableName();
    }

    /**
     * Pomocna metoda pro konverzi z intu do booleanu
     */
    protected boolean convertToBoolean(int i) {

        return i != 0;

    }

}

package eu.janmuller.android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Map;

public interface IBaseDao<T extends AbstractModel> {

    /**
     * Obecne atributy (sloupecky v tabulkach) pro vsechny DAO podtridy
     */
    public static final String KEY_ID = "l_id";
    public static final String KEY_CREATION_DATE = "l_creation_date";
    public static final String KEY_MODIFY_DATE = "l_modify_date";


    /**
     * Vytvori nebo updatuje objekt
     *
     * @return UUID identifikator, pokud nic nevrati, pak doslo k chybe
     */
    public Object insertOrUpdate(T object);

    public Object insert(T object);
    /**
     * Vraci objekt z DB dle zadaneho UUID
     */
    public T retrieveById(Object id);

    /**
     * Vraci vsechny objekty z DB
     *
     * @return vraci seznam, nikdy ne NULL
     */
    public List<T> retrieveAll();

    /**
     * Vraci vsechny objekty v Cursoru
     */
    public Cursor retrieveAllInCursor();

    /**
     * Vraci seznam objektu na zaklade predane where klauzule
     */
    public List<T> retrieveByQuery(String whereClause);

    /**
     * Vraci seznam objektu v Cursoru na zaklade predane where klauzule
     */
    public Cursor retrieveByQueryInCursor(String whereClause);

    /**
     * Vraci mapu objektu, kde klicem je UUID id objektu a hodnotou je cely objekt
     */
    public Map<Object, T> getAllObjectsAsMap();

    /**
     * Vymaze objekt z DB
     *
     * @throws eu.janmuller.android.dao.exceptions.DaoConstraintException v pripade, ze smazanim by doslo k poruseni referencni integrity
     */
    public void delete(T object);

    /**
     * Vymaze vsechny objekty z tabulky danou parametrem
     *
     * @throws eu.janmuller.android.dao.exceptions.DaoConstraintException v pripade, ze smazanim by doslo k poruseni referencni integrity
     */
    public void deleteAll();

    /**
     * Vymaze vsechny objekty specifikovane where klauzuli
     */
    public void deleteByQuery(String whereClause);

    /**
     * Vytvori a vrati sql pro vytvoreni tabulky
     */
    public void createTable(SQLiteDatabase db);

    /**
     * Vytvori a vrati sql pro vymazani tabulky
     */
    public void dropTable(SQLiteDatabase db);


}

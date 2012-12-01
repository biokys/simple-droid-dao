package eu.janmuller.android.dao.api;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: biokys
 * Date: 26.3.12
 * Time: 0:37
 * <p/>
 * Trida slouzi pro vytvareni SQL prikazu, pomoci ktereho se vytvori tabulka,
 * vytvori se sloupce tabulky, cizy klice, indexy
 */
class CreateTableSqlBuilder {

    private String mSql;

    private List<String> mPartialSqls = new ArrayList<String>();
    private List<String> mConstraintSqls = new ArrayList<String>();
    private List<String> mUniqueColumns = new ArrayList<String>();
    private String mUniqueSql;

    private List<String> mCreateIndexCommands;

    private String mTableName;

    private static final String DATA_TYPE_TEXT = "text";
    private static final String DATA_TYPE_INTEGER = "integer";
    private static final String DATA_TYPE_REAL = "real";
    private static final String DATA_TYPE_BLOB = "blob";

    public enum Conflicts {

        /**
         * When an applicable constraint violation occurs, the ROLLBACK resolution algorithm aborts the current SQL statement
         * with an SQLITE_CONSTRAINT error and rolls back the current transaction.
         * If no transaction is active (other than the implied transaction that is created on every command)
         * then the ROLLBACK resolution algorithm works the same as the ABORT algorithm.
         */
        ROLLBACK("ROLLBACK"),

        /**
         * When an applicable constraint violation occurs, the ABORT resolution algorithm aborts the current SQL statement
         * with an SQLITE_CONSTRAIT error and backs out any changes made by the current SQL statement;
         * but changes caused by prior SQL statements within the same transaction are preserved and the transaction remains active.
         * This is the default behavior and the behavior proscribed the SQL standard.
         */
        ABORT("ABORT"),

        /**
         * When an applicable constraint violation occurs, the FAIL resolution algorithm aborts the current SQL statement with an SQLITE_CONSTRAINT error.
         * But the FAIL resolution does not back out prior changes of the SQL statement that failed nor does it end the transaction.
         * For example, if an UPDATE statement encountered a constraint violation on the 100th row that it attempts to update,
         * then the first 99 row changes are preserved but changes to rows 100 and beyond never occur.
         */
        FAIL("FAIL"),

        /**
         * When an applicable constraint violation occurs, the IGNORE resolution algorithm skips the one row that contains
         * the constraint violation and continues processing subsequent rows of the SQL statement as if nothing went wrong.
         * Other rows before and after the row that contained the constraint violation are inserted or updated normally.
         * No error is returned when the IGNORE conflict resolution algorithm is used.
         */
        IGNORE("IGNORE"),

        /**
         * When a UNIQUE constraint violation occurs, the REPLACE algorithm deletes pre-existing rows that are causing the constraint violation prior
         * to inserting or updating the current row and the command continues executing normally. If a NOT NULL constraint violation occurs,
         * the REPLACE conflict resolution replaces the NULL value with he default value for that column, or if the column has no default value,
         * then the ABORT algorithm is used. If a CHECK constraint violation occurs, the REPLACE conflict resolution algorithm always works like ABORT.
         */
        REPLACE("REPLACE");

        private String conflict;

        Conflicts(String conflict) {

            this.conflict = conflict;
        }

        public String getConflict() {

            return conflict;
        }
    }

    protected CreateTableSqlBuilder(String tableName) {

        mCreateIndexCommands = new ArrayList<String>();
        mTableName = tableName;
        mSql = "create table if not exists " + tableName + "(";
    }

    /**
     * Vytvori textovy atribut
     *
     * @param name    jmeno atributu
     * @param notnull priznak, zda atribut musi, ci nemusi byt nastaven
     */
    public CreateTableSqlBuilder addTextColumn(String name, boolean notnull) {

        createColumn(DATA_TYPE_TEXT, name, notnull);
        return this;
    }

    /**
     * Vytvori textovy atribut (povinny)
     *
     * @param name    jmeno atributu
     */
    public CreateTableSqlBuilder addTextColumn(String name) {

        return addTextColumn(name, true);
    }

    /**
     * Vytvori atribut pro realne cislo
     *
     * @param name    jmeno atributu
     * @param notnull priznak, zda atribut musi, ci nemusi byt nastaven
     */
    public CreateTableSqlBuilder addRealColumn(String name, boolean notnull) {

        createColumn(DATA_TYPE_REAL, name, notnull);
        return this;
    }

    /**
     * Vytvori atribut pro realne cislo (povinny)
     *
     * @param name    jmeno atributu
     */
    public CreateTableSqlBuilder addRealColumn(String name) {

        return addRealColumn(name, true);
    }

    /**
     * Vytvori ciselny (integer) atribut
     *
     * @param name    jmeno atributu
     * @param notnull priznak, zda atribut musi, ci nemusi byt nastaven
     */
    public CreateTableSqlBuilder addIntegerColumn(String name, boolean notnull) {

        createColumn(DATA_TYPE_INTEGER, name, notnull);
        return this;
    }

    /**
     * Vytvori ciselny (integer) atribut (povinny)
     *
     * @param name    jmeno atributu
     */
    public CreateTableSqlBuilder addIntegerColumn(String name) {

        return addIntegerColumn(name, true);
    }

    /**
     * Vytvori BLOB atribut
     *
     * @param name    jmeno atributu
     * @param notnull priznak, zda atribut musi, ci nemusi byt nastaven
     */
    public CreateTableSqlBuilder addBlobColumn(String name, boolean notnull) {

        createColumn(DATA_TYPE_BLOB, name, notnull);
        return this;
    }

    /**
     * Vytvori BLOB atribut (povinny)
     *
     * @param name    jmeno atributu
     */
    public CreateTableSqlBuilder addBlobColumn(String name) {

        return addBlobColumn(name, true);
    }

    private void createColumn(String dataType, String name, boolean notNull) {

        mPartialSqls.add(name + " " + dataType + " " + (notNull ? "not null" : ""));
    }

    /**
     * Vytvori foreign key constraint na atributu
     *
     * @param attributeName    jmeno atributu
     * @param foreignTableName tabulka, ve ktere se nachazi cizi klic
     * @param foreignAttribute jmeno atributu, ktery slouzi jako cizy klic
     */
    //TODO: automaticky pridavat na foreign key i INDEX
    public CreateTableSqlBuilder addForeignKey(String attributeName, String foreignTableName, String foreignAttribute) {

        return addForeignKey(attributeName, foreignTableName, foreignAttribute, false);
    }

    /**
     * Vytvori foreign key constraint na atributu
     *
     * @param attributeName    jmeno atributu
     * @param foreignTableName tabulka, ve ktere se nachazi cizi klic
     * @param foreignAttribute jmeno atributu, ktery slouzi jako cizy klic
     * @param cascadeDelete    zapina/vypina cascade delete
     */
    //TODO: automaticky pridavat na foreign key i INDEX
    public CreateTableSqlBuilder addForeignKey(String attributeName, String foreignTableName, String foreignAttribute, boolean cascadeDelete) {

        return addForeignKey(attributeName, foreignTableName, foreignAttribute, cascadeDelete, false);
    }

    /**
     * Vytvori foreign key constraint na atributu
     *
     * @param attributeName    jmeno atributu
     * @param foreignTableName tabulka, ve ktere se nachazi cizi klic
     * @param foreignAttribute jmeno atributu, ktery slouzi jako cizy klic
     * @param cascadeDelete    zapina/vypina cascade delete
     */
    //TODO: automaticky pridavat na foreign key i INDEX
    public CreateTableSqlBuilder addForeignKey(String attributeName, String foreignTableName, String foreignAttribute, boolean cascadeDelete, boolean cascadeUpdate) {

        String cascadeDeleteString = "";
        String cascadeUpdateString = "";

        String cascadeString;

        if (cascadeDelete) {

            cascadeDeleteString = " ON DELETE CASCADE";
        }

        if (cascadeUpdate) {

            cascadeUpdateString = " ON UPDATE CASCADE";
        }

        cascadeString = cascadeDeleteString + cascadeUpdateString;

        mConstraintSqls.add(" CONSTRAINT FK_" + attributeName + "__" + foreignTableName + "_" + foreignAttribute + " FOREIGN KEY(" + attributeName + ") REFERENCES " +
                foreignTableName + "(" + foreignAttribute + ")" + cascadeString + " ");
        return this;
    }

    /**
     * Vytvori jednoduchy index nad aktualni tabulkou
     *
     * @param attributeName jmeno atributu, nad kterym mame index
     */
    public CreateTableSqlBuilder addSimpleIndex(String attributeName) {

        String createIndexCommand = "CREATE INDEX IF NOT EXISTS " + this.mTableName + "_" + attributeName + "_idx ON " + this.mTableName + "(" + attributeName + ");";

        this.mCreateIndexCommands.add(createIndexCommand);

        return this;
    }

    /**
     * Vytvori UNIQUE constrain na jednom nebo vice sloupeccich
     *
     * @param attributeName pole nazvu sloupcu
     */
    public CreateTableSqlBuilder addUniqueConstrain(String attributeName) {

        mUniqueColumns.add(attributeName);

        return this;
    }

    private void createPrimaryColumn(String dataType, String name, boolean autoincrement) {

        mPartialSqls.add(name + " " + dataType + " not null primary key" + (autoincrement ? " autoincrement " : " "));
    }

    public CreateTableSqlBuilder addIntegerPrimaryColumn(String name) {

        createPrimaryColumn(DATA_TYPE_INTEGER, name, true);
        return this;
    }

    public CreateTableSqlBuilder addTextPrimaryColumn(String name) {

        createPrimaryColumn(DATA_TYPE_TEXT, name, false);
        return this;
    }



    /**
     * Metoda vytvori vysledne sql
     *
     * @return SQL, pomoci ktereho vytvorime tabulku v DB
     */
    String create() {

        String[] partials = mPartialSqls.toArray(new String[mPartialSqls.size()]);
        String[] constraints = mConstraintSqls.toArray(new String[mConstraintSqls.size()]);
        String par = join(',', partials);

        if (mConstraintSqls.size() > 0) {

            par += "," + join(',', constraints);
        }

        mSql += par;

        String[] uniques = mUniqueColumns.toArray(new String[mUniqueColumns.size()]);
        if (uniques != null && uniques.length > 0) {

            mSql += ", UNIQUE(" + join(',', uniques) + ") ";
        }

        mSql += ");";

        if (!mCreateIndexCommands.isEmpty()) {

            for (String command : mCreateIndexCommands) {

                mSql += command;
            }
        }

        mPartialSqls.clear();
        mConstraintSqls.clear();
        mUniqueColumns.clear();

        return mSql;
    }

    private String join(char separator, String... array) {

        if (array == null) {
            return null;
        }

        int arraySize = array.length;
        int bufSize = (arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0].length()) + 1) * arraySize);
        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }


}

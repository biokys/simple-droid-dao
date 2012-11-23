package eu.janmuller.android.dao.api;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.janmuller.android.dao.DaoConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public final class SimpleDroidDao {

    private static String mDatabaseName;

    private static int mDatabaseVersion;

    private static DatabaseHelper databaseHelper;

    private static IUpgradeHandler mUpgradeHandler;

    private static List<Class<? extends BaseModel>> sModelClasses = new ArrayList<Class<? extends BaseModel>>();


    private SimpleDroidDao() {}

    public static void initialize(Context context, String databaseName, int version, IUpgradeHandler upgradeHandler) {

        mDatabaseName = databaseName;
        mDatabaseVersion = version;
        mUpgradeHandler = upgradeHandler;
        databaseHelper = new DatabaseHelper(context);
    }

    public static void registerModelClass(Class<? extends BaseModel> clazz) {

        sModelClasses.add(clazz);
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, mDatabaseName, null, mDatabaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.i(DaoConstants.LOG_TAG, "Creating tables...");


            for (Class<? extends BaseModel> model : sModelClasses) {

                createTable(db, model);
            }


            Log.i(DaoConstants.LOG_TAG, "tables created succesfully");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.i(DaoConstants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ")");

            if (mUpgradeHandler == null) {
                for (Class<? extends BaseModel> model : sModelClasses) {

                    dropTable(db, model);
                    createTable(db, model);
                }

            } else {

                mUpgradeHandler.onUpgrade(db, oldVersion, newVersion);
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

            // pokud neni db v readonly modu
            if (!db.isReadOnly()) {

                // zapneme foreign keys
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
    }

    static <T extends BaseModel> void createTable(SQLiteDatabase db, Class<T> clazz) {

        db.execSQL(GenericModel.getCreateTableSQL(clazz));
    }

    static <T extends BaseModel> void dropTable(SQLiteDatabase db, Class<T> clazz) {

        db.execSQL("drop table if exists " + GenericModel.getTableName(clazz));
    }

    public static SQLiteDatabase getOpenedDatabase(Class clazz) {

        /*if (!sModelClasses.contains(clazz)) {

            throw new IllegalStateException("You have to call register method before for class " + clazz);
        }*/

        return databaseHelper.getWritableDatabase();
    }

    static SQLiteDatabase getOpenedDatabase() {

        return databaseHelper.getWritableDatabase();
    }

    public interface IUpgradeHandler {

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}

package eu.janmuller.android.dao.api;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for creating SimpleDroidDao context
 */
public final class SimpleDroidDao {

    public static final String LOG_TAG = "dao";

    private static String          sDatabaseName;
    private static int             sDatabaseVersion;
    private static DatabaseHelper  sDatabaseHelper;
    private static IUpgradeHandler sUpgradeHandler;

    private static List<Class<? extends BaseModel>> sModelClasses = new ArrayList<Class<? extends BaseModel>>();

    private SimpleDroidDao() {

    }

    /**
     * Initialice SDD context by this method. All Model classes must be registered before calling this method
     * @param context
     * @param databaseName Filename of your DB
     * @param version DB version
     * @param upgradeHandler Callback instance for managing db version upgrades
     */
    public static void initialize(Context context, String databaseName, int version, IUpgradeHandler upgradeHandler) {

        sDatabaseName = databaseName;
        sDatabaseVersion = version;
        sUpgradeHandler = upgradeHandler;
        sDatabaseHelper = new DatabaseHelper(context);
    }

    /**
     * Every model class must be registered by this method. It must be called before initialize() method
     * @param clazz
     */
    public static void registerModelClass(Class<? extends BaseModel> clazz) {

        sModelClasses.add(clazz);
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, sDatabaseName, null, sDatabaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.i(LOG_TAG, "Creating tables...");


            for (Class<? extends BaseModel> model : sModelClasses) {

                createTable(db, model);
            }

            Log.i(LOG_TAG, "tables created succesfully");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.i(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ")");

            if (sUpgradeHandler == null) {

                for (Class<? extends BaseModel> model : sModelClasses) {

                    dropTable(db, model);
                    createTable(db, model);
                }

            } else {

                sUpgradeHandler.onUpgrade(db, oldVersion, newVersion);
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {

            super.onOpen(db);

            if (!db.isReadOnly()) {

                // turn on foreign keys
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

        return sDatabaseHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getOpenedDatabaseForReading() {

        return sDatabaseHelper.getReadableDatabase();
    }

    static SQLiteDatabase getOpenedDatabase() {

        return sDatabaseHelper.getWritableDatabase();
    }

    public interface IUpgradeHandler {

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}

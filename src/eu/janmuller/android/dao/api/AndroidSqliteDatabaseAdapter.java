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
public final class AndroidSqliteDatabaseAdapter {

    private String mDatabaseName;

    private int mDatabaseVersion;

    private DatabaseHelper databaseHelper;

    private Context mContext;

    private IUpgradeHandler mUpgradeHandler;

    private static AndroidSqliteDatabaseAdapter INSTANCE;

    private List<Class<? extends BaseModel>> mModelClasses = new ArrayList<Class<? extends BaseModel>>();


    private AndroidSqliteDatabaseAdapter(Context context, String databaseName, int version, IUpgradeHandler upgradeHandler) {

        mDatabaseName = databaseName;
        mDatabaseVersion = version;
        mContext = context;
        mUpgradeHandler = upgradeHandler;

    }

    public static synchronized void initialize(Context context, String databaseName, int version, IUpgradeHandler upgradeHandler) {

        if (INSTANCE == null) {

            INSTANCE = new AndroidSqliteDatabaseAdapter(context, databaseName, version, upgradeHandler);
        }
    }

    public void registerClass(Class<? extends BaseModel> clazz) {

        mModelClasses.add(clazz);
    }

    public void start() {

        databaseHelper = new DatabaseHelper(this);
    }

    public static AndroidSqliteDatabaseAdapter getInstance() {

        if (INSTANCE == null) {

            throw new IllegalStateException("you have to call initialize method before");
        }
        return INSTANCE;
    }

    class DatabaseHelper extends SQLiteOpenHelper {

        AndroidSqliteDatabaseAdapter mDatabaseAdapter;

        DatabaseHelper(AndroidSqliteDatabaseAdapter databaseAdapter) {

            super(databaseAdapter.mContext, mDatabaseName, null, mDatabaseVersion);
            mDatabaseAdapter = databaseAdapter;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.i(DaoConstants.LOG_TAG, "Creating tables...");


            for (Class<? extends BaseModel> model : mModelClasses) {

                createTable(db, model);
            }


            Log.i(DaoConstants.LOG_TAG, "tables created succesfully");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.i(DaoConstants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ")");

            if (mUpgradeHandler == null) {
                for (Class<? extends BaseModel> model : mModelClasses) {

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

    public SQLiteDatabase getOpenedDatabase(Class clazz) {

        /*if (!mModelClasses.contains(clazz)) {

            throw new IllegalStateException("You have to call register method before for class " + clazz);
        }*/

        return databaseHelper.getWritableDatabase();
    }

    public interface IUpgradeHandler {

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}

package eu.janmuller.android.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public final class DatabaseAdapter {

    private static String mDatabaseName;

    private static int mDatabaseVersion;

    private DatabaseHelper databaseHelper;

    private Context mContext;

    private IUpgradeHandler mIUpgradeHandler;

    public DatabaseAdapter(Context context, String databaseName, int version) {

        mDatabaseName = databaseName;
        mDatabaseVersion = version;
        mContext = context;

        databaseHelper = new DatabaseHelper(this);
    }

    public void setOnUpgradeHandler(IUpgradeHandler upgradeHandler) {

        this.mIUpgradeHandler = upgradeHandler;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseAdapter mDatabaseAdapter;

        DatabaseHelper(DatabaseAdapter databaseAdapter) {

            super(databaseAdapter.mContext, mDatabaseName, null, mDatabaseVersion);
            mDatabaseAdapter = databaseAdapter;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.i(DaoConstants.LOG_TAG, "Creating tables...");

            for (IBaseDao daos : DaoService.getAllDaoInstances()) {
                daos.createTable(db);
            }

            Log.i(DaoConstants.LOG_TAG, "tables created succesfully");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.i(DaoConstants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ")");

            List<IBaseDao> list = DaoService.getAllDaoInstances();

            if (mDatabaseAdapter.mIUpgradeHandler != null) {

                mDatabaseAdapter.mIUpgradeHandler.onUpgrade(db, oldVersion, newVersion);
            } else {

                // pro spravne dodrzeni constraints prevratim poradi dropovani tabulek
                Collections.reverse(list);

                for (IBaseDao daos : list) {
                    daos.dropTable(db);
                }

                for (IBaseDao daos : DaoService.getAllDaoInstances()) {
                    daos.createTable(db);
                }
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

    public SQLiteDatabase getOpenedDatabase() {
        return databaseHelper.getWritableDatabase();
    }

    public interface IUpgradeHandler {

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}

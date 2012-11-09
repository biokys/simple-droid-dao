package eu.janmuller.android.dao.api;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.janmuller.android.dao.DaoConstants;
import eu.janmuller.android.dao.IDatabaseAdapter;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public final class AndroidSqliteDatabaseAdapter implements IDatabaseAdapter {

    private static String mDatabaseName;

    private static int mDatabaseVersion;

    private DatabaseHelper databaseHelper;

    private Context mContext;

    public AndroidSqliteDatabaseAdapter(Context context, String databaseName, int version) {

        mDatabaseName = databaseName;
        mDatabaseVersion = version;
        mContext = context;

        databaseHelper = new DatabaseHelper(this);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        AndroidSqliteDatabaseAdapter mDatabaseAdapter;

        DatabaseHelper(AndroidSqliteDatabaseAdapter databaseAdapter) {

            super(databaseAdapter.mContext, mDatabaseName, null, mDatabaseVersion);
            mDatabaseAdapter = databaseAdapter;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.i(DaoConstants.LOG_TAG, "Creating tables...");



            Log.i(DaoConstants.LOG_TAG, "tables created succesfully");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.i(DaoConstants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ")");



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

    @Override
    public IDatabaseProvider getDatabaseProvider() {
        return new IDatabaseProvider<Cursor>() {
            @Override
            public Cursor query(String whereSql, String[] params) {
                return getOpenedDatabase().rawQuery(whereSql, params);
            }

            @Override
            public void execSQL(String sql) {

                getOpenedDatabase().execSQL(sql);
            }

            @Override
            public long insertOrThrow(String name, ContentValues cv) {

                return getOpenedDatabase().insertOrThrow(name, null, cv);
            }

            @Override
            public long update(String name, ContentValues cv, String id) {

                return getOpenedDatabase().update(name, cv, id, null);
            }
        };
    }



    public SQLiteDatabase getOpenedDatabase() {
        return databaseHelper.getWritableDatabase();
    }

    public interface IUpgradeHandler {

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}

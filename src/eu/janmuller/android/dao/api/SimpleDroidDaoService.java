package eu.janmuller.android.dao.api;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 07.11.12
 * Time: 16:18
 */
public class SimpleDroidDaoService implements ISimpleDroidDaoService {

    AndroidSqliteDatabaseAdapter databaseAdapter;

    AndroidSqliteDatabaseAdapter getDao() {

        if (databaseAdapter == null) {

            throw new IllegalStateException("You have to call setDao method before");
        }
        return databaseAdapter;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDao(Context context, String dbName, int version) {

        databaseAdapter = new AndroidSqliteDatabaseAdapter(context, dbName, version);
    }
}

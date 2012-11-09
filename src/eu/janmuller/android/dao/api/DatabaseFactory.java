package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 09.11.12
 * Time: 16:54
 */
public class DatabaseFactory {

    final private static DatabaseFactory INSTANCE = new DatabaseFactory();

    private IDatabaseProvider mDatabaseProvider;

    public DatabaseFactory() {
    }

    public static DatabaseFactory getInstance() {

        return INSTANCE;
    }

    public void setDatabaseProvider(IDatabaseProvider databaseProvider) {

        mDatabaseProvider = databaseProvider;
    }

    public IDatabaseProvider getDatabaseProvider() {

        if (mDatabaseProvider == null) {

            throw new IllegalStateException();
        }
        return mDatabaseProvider;
    }

}

package eu.janmuller.android.dao.exceptions;

import android.database.sqlite.SQLiteConstraintException;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 01.12.12
 * Time: 22:52
 */
final public class ConstraintExceptionFactory {

    private ConstraintExceptionFactory() {}

    public static SimpleDroidDaoException getException(SQLiteConstraintException e) {

        if (e.getMessage().contains("unique")) {

            return new SimpleDroidDaoUniqueConstraintException(e);
        } else if (e.getMessage().contains("NULL")) {

            return new SimpleDroidDaoNotnullConstraintException(e);
        } else {

            return new SimpleDroidDaoException(e);
        }
    }
}

package eu.janmuller.android.dao.exceptions;

import android.database.sqlite.SQLiteConstraintException;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 01.12.12
 * Time: 22:54
 */
public class SimpleDroidDaoNotnullConstraintException extends SimpleDroidDaoException {

    public SimpleDroidDaoNotnullConstraintException(SQLiteConstraintException e) {

        super(e.getMessage());
    }
}

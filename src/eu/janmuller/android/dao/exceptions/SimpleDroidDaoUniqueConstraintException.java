package eu.janmuller.android.dao.exceptions;

import android.database.sqlite.SQLiteConstraintException;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 01.12.12
 * Time: 22:49
 */
public class SimpleDroidDaoUniqueConstraintException extends SimpleDroidDaoException {

    public SimpleDroidDaoUniqueConstraintException(SQLiteConstraintException e) {

        super(e.getMessage());
    }
}

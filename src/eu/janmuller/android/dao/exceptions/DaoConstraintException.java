package eu.janmuller.android.dao.exceptions;

import android.database.sqlite.SQLiteConstraintException;

/**
 * Created by IntelliJ IDEA.
 * User: biokys
 * Date: 26.3.12
 * Time: 11:47
 */
public class DaoConstraintException extends RuntimeException {

    public enum ConstraintsExceptionType {

        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete");

        private String type;

        private ConstraintsExceptionType(String type) {

            this.type = type;
        }

        public String getDetailMessage() {

            return type + " constraint exception";
        }
    }

    ConstraintsExceptionType constraintsExceptionType;

    public DaoConstraintException(ConstraintsExceptionType constraintsExceptionType, SQLiteConstraintException e) {

        super(constraintsExceptionType.getDetailMessage(), e);
        this.constraintsExceptionType = constraintsExceptionType;
    }

    public ConstraintsExceptionType getConstraintDetail() {

        return constraintsExceptionType;
    }
}

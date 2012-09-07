package eu.janmuller.android.dao.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: biokys
 * Date: 26.3.12
 * Time: 11:47
 */
public class DaoException extends RuntimeException{

    public DaoException(String detailMessage) {
        super(detailMessage);
    }
}

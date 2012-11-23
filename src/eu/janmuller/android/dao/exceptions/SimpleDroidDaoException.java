package eu.janmuller.android.dao.exceptions;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 22.11.12
 * Time: 19:07
 */
public class SimpleDroidDaoException extends RuntimeException {

    public SimpleDroidDaoException(String detailMessage) {
        super(detailMessage);
    }

    public SimpleDroidDaoException(Throwable throwable) {
        super(throwable);
    }

    public SimpleDroidDaoException() {
    }
}

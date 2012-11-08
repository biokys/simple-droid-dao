package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:35
 */
public interface ISimpleDroidDao<T> {

    public T save();

    public boolean delete();

    public boolean deleteAll();
}

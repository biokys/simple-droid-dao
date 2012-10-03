package eu.janmuller.android.dao.api;


/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:35
 */
public interface ISimpleDroidDaoService {

    public void registerDaoClass(Class daoClass, Class modelClass);

    public ISimpleDroidDao getInstance(Class daoClass);
}

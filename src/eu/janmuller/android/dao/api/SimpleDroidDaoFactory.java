package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:36
 */
final public class SimpleDroidDaoFactory {

    final static private ISimpleDroidDaoService INSTANCE = new SimpleDroidDaoService();

    public static ISimpleDroidDaoService getInstance() {

        return INSTANCE;
    }

}

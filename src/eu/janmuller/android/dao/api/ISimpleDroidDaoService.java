package eu.janmuller.android.dao.api;


import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:35
 */
public interface ISimpleDroidDaoService {

    public void setDao(Context context, String dbName, int version);
}

package eu.janmuller.android.dao.api;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 09.11.12
 * Time: 17:01
 */
public interface AndroidSqlConfig {

    public Context getContext();

    public String getDbName();

    public int getDbVersion();
}

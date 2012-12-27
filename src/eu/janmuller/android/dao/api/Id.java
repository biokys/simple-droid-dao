package eu.janmuller.android.dao.api;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:50
 */
public interface Id<U> extends Serializable {

    public U getId();

    public boolean isPrimaryKey();

}

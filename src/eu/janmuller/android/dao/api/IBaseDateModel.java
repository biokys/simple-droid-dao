package eu.janmuller.android.dao.api;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 14:09
 */
public interface IBaseDateModel<U> extends Id<U> {

    public Date getCreationDate();

    public Date getModifiedDate();
}

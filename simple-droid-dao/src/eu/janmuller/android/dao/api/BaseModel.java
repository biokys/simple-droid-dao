package eu.janmuller.android.dao.api;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 14:43
 */
public abstract class BaseModel<T extends BaseModel> extends GenericModel<T> implements Serializable {

    @InternalFieldType(type = SimpleDaoSystemFieldsEnum.ID)
    public Id id;

}

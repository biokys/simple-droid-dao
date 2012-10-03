package eu.janmuller.android.dao.api;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 14:45
 */
public class BaseDateModel<T, U> extends BaseModel<T, U> implements IBaseDateModel<U> {

    @GenericModel.InternalFieldType(type = GenericModel.SimpleDaoSystemFieldsEnum.CREATE)
    public Date creationDate;

    @GenericModel.InternalFieldType(type = SimpleDaoSystemFieldsEnum.MODIFY)
    public Date modifiedDate;

    @Override
    public Date getCreatedDate() {
        return null;
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

}

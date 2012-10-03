package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 14:43
 */
public abstract class BaseModel<T, U> extends GenericModel<T> implements Id<U> {

    @InternalFieldType(type = SimpleDaoSystemFieldsEnum.ID)
    public U id;

    @Override
    public U getId() {

        return null;//id.getId();
    }
}

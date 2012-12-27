package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:55
 */
abstract public class AbstractId<T> implements Id<T> {

    public enum OperationType {

        UPDATE,
        CREATE
    }

    protected T mId;

    boolean mPrimaryKey = false;

    protected AbstractId(T t) {

        this.mId = t;
    }

    @Override
    public T getId() {

        return mId;
    }

    @Override
    public boolean isPrimaryKey() {
        return mPrimaryKey;
    }

    /**
     * Whether to create or update
     */
    abstract OperationType operationType();



}

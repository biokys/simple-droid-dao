package eu.janmuller.android.dao.api.id;

import eu.janmuller.android.dao.api.id.Id;

import java.io.Serializable;

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

    protected T t;

    protected AbstractId(T t) {

        this.t = t;
    }

    @Override
    public T getId() {

        return t;
    }

    /**
     * Whether to create or update
     */
    public abstract OperationType operationType();

}

package eu.janmuller.android.dao.api.id;

import eu.janmuller.android.dao.api.id.Id;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:55
 */
abstract public class AbstractId<T> implements Id<T> {

    private T t;

    protected AbstractId(T t) {

        this.t = t;
    }

    @Override
    public T getId() {

        return t;
    }
}

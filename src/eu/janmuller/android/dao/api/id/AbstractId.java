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

    protected T t;

    protected AbstractId(T t) {

        this.t = t;
    }

    @Override
    public T getId() {

        return t;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractId that = (AbstractId) o;

        if (t != null ? !t.equals(that.t) : that.t != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }*/
}

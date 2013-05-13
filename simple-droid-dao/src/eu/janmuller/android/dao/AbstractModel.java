package eu.janmuller.android.dao;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 8/15/12
 * Time: 9:27 AM
 */
public abstract class AbstractModel<T> {


    /**
     * Datum kdy byl objekt vytvoren
     */
    public Date created;

    /**
     * Datum kdy doslo ke zmene objektu
     */
    public Date modified;

    protected abstract T getId();

    protected abstract void setId(T t);

    protected abstract boolean isNew();

    protected abstract T getNewId();

    @Override
    public String toString() {
        return "AbstractModel{" +
                "created=" + created +
                ", modified=" + modified +
                '}';
    }
}

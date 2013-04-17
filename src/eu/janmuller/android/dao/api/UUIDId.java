package eu.janmuller.android.dao.api;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 15:17
 */
public class UUIDId extends AbstractId<String> {

    // pomocna promenna, ktera slouzi jako pomocny prostredek k rozpoznani, zda objekt ulozit, nebo updatovat
    transient boolean create;

    transient boolean manuallySet;

    String id;

    public UUIDId(String id) {
        super(id);
    }

    public UUIDId() {

        super(getUUID());
    }

    private static String getUUID() {

        return UUID.randomUUID().toString();
    }

    /**
     * Usefull when you want to set UUID manually and insert new object (not update)
     */
    public void manuallySetId(boolean manually) {

        manuallySet = manually;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    OperationType operationType() {

        return create ? OperationType.CREATE : OperationType.UPDATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UUIDId uuidId = (UUIDId) o;

        if (mId != null ? !mId.toString().equals(uuidId.mId.toString()) : uuidId.mId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mId != null ? mId.hashCode() : 0;
    }
}

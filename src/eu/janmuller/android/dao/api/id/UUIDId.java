package eu.janmuller.android.dao.api.id;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 15:17
 */
public class UUIDId extends AbstractId<String> {

    // pomocna promenna, ktera slouzi jako pomocny prostredek k rozpoznani, zda objekt ulozit, nebo updatovat
    public boolean create;

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

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public OperationType operationType() {

        return create ? OperationType.CREATE : OperationType.UPDATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UUIDId uuidId = (UUIDId) o;

        if (t != null ? !t.equals(uuidId.t) : uuidId.t != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }
}

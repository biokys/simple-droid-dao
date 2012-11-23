package eu.janmuller.android.dao.api.id;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:58
 */
final public class LongId extends AbstractId<Long> {

    public LongId(Long id) {

        super(id);
    }

    @Override
    public String toString() {

        return getId().toString();
    }

    @Override
    public OperationType operationType() {

        return getId() == 0l ? OperationType.CREATE : OperationType.UPDATE;
    }
}

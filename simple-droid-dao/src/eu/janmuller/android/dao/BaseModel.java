package eu.janmuller.android.dao;

/**
 * Bazova trida pro vsechny objekty datoveho modelu
 *
 * @author biokys
 */
public class BaseModel extends AbstractModel<Long> {

    /**
     * Autoincrement Id
     */
    public Long id;

    @Override
    public String toString() {

        return "(id: " + id + ", created: " + created + ", modified: " + modified + ")";
    }

    @Override
    final protected Long getId() {

        return id;
    }

    @Override
    final protected void setId(Long id) {

        this.id = id;
    }

    @Override
    final protected boolean isNew() {

        return (id == null || id < 0);
    }

    @Override
    final protected Long getNewId() {

        return null;
    }
}

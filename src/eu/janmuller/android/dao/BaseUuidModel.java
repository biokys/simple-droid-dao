package eu.janmuller.android.dao;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 8/15/12
 * Time: 9:27 AM
 */
public class BaseUuidModel extends AbstractModel<String> {

    String id;

    /**
     * Metoda, ktera vytvari unikatni identifikator
     */
    private static String getUUID() {

        return UUID.randomUUID().toString();
    }

    @Override
    final protected String getId() {

        return id;
    }

    @Override
    final protected void setId(String id) {

        this.id = id;
    }

    @Override
    final protected boolean isNew() {

        return (id == null || id.length() == 0);
    }

    @Override
    final protected String getNewId() {

        return getUUID();
    }

    @Override
    public String toString() {
        return "BaseUuidModel{" +
                "id='" + id + '\'' +
                '}' + super.toString();
    }
}

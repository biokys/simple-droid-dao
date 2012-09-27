package eu.janmuller.android.dao;

import android.util.Log;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 8/15/12
 * Time: 9:27 AM
 */
public class BaseUuidModel extends AbstractModel<String> implements Serializable {

    private static final String LOG_TAG = "BaseModel";

    public String id;

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

    /**
     * Metoda, ktera primarne slouzi pro ucely vybirani polozek ve spinneru
     * Vytvori instanci na zaklade parametru clazz s nastavenym id na zaklade parametru
     * id. Pokud je parametr id = null, pak vraci null instanci
     */
    public static <T extends BaseUuidModel> T getInstanceWithId(Class<T> clazz, String id) {

        if (id == null) {

            return null;
        }

        T instance = null;

        try {

            instance = clazz.newInstance();
            instance.id = id;
        } catch (IllegalAccessException e) {

            Log.e(LOG_TAG, "error while making instance of class " + clazz.getName(), e);
        } catch (InstantiationException e) {

            Log.e(LOG_TAG, "error while making instance of class " + clazz.getName(), e);
        }

        return instance;
    }
}

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
    boolean create;

    public UUIDId(String id) {
        super(id);
    }

    public UUIDId() {

        super(getUUID());
    }

    private static String getUUID() {

        return UUID.randomUUID().toString();
    }
}

package eu.janmuller.android.dao.api;

import android.content.ContentValues;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 09.11.12
 * Time: 16:41
 */
public interface IDatabaseProvider<T> {

    public T query(String whereSql, String[] params);

    public void execSQL(String sql);

    public long insertOrThrow(String name, ContentValues cv);

    public long update(String name, ContentValues cv, String id);

}

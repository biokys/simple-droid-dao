package eu.janmuller.android.dao.api;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import eu.janmuller.android.dao.exceptions.ConstraintExceptionFactory;
import eu.janmuller.android.dao.exceptions.DaoConstraintException;
import eu.janmuller.android.dao.exceptions.SimpleDroidDaoException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:48
 */
public abstract class GenericModel<T extends BaseModel> {

    private static Map<Class, IdTypeEnum>                 sIdTypeEnumMap         = new HashMap<Class, IdTypeEnum>();
    private static Map<Class, String>                     sTableNameMap          = new HashMap<Class, String>();
    private static Map<Class, Boolean>                    sExcludeFromNtfNameMap = new HashMap<Class, Boolean>();
    private static Map<String, DataTypeEnum>              sDataTypeCache         = new HashMap<String, DataTypeEnum>();
    private static Map<String, SimpleDaoSystemFieldsEnum> sInternalFieldCache    = new HashMap<String, SimpleDaoSystemFieldsEnum>();
    private static Map<Class, List<Field>>                sFieldsCache           = new HashMap<Class, List<Field>>();
    private static Map<String, Object[]>                  sEnumCache             = new HashMap<String, Object[]>();
    private static Date                                   sDate                  = new Date();

    private transient SimpleDroidDaoEventMonitor mEventMonitor = SimpleDroidDaoEventMonitor.getInstance();

    public static void beginTx() {

        getSQLiteDatabase().beginTransaction();
    }

    /**
     * Begin non-exclusive transaction, if the API is 11 and higher. If API < 11, then exclusive transaction is used
     */
    public static void beginNonExclusiveTx() {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            getSQLiteDatabase().beginTransactionNonExclusive();
        } else {

            beginTx();
        }
    }

    public static void endTx() {

        getSQLiteDatabase().endTransaction();
    }

    public static void setTxSuccesfull() {

        getSQLiteDatabase().setTransactionSuccessful();
    }

    public static <T extends BaseModel> T findObjectById(Class<T> clazz, Id id) {

        if (id == null) {

            throw new SimpleDroidDaoException("no id specified");
        }

        Cursor cursor = getSQLiteDatabase().rawQuery("SELECT * FROM " + getTableName(clazz)
                + " WHERE " + SimpleDaoSystemFieldsEnum.ID + "=? LIMIT 1", new String[]{id.getId().toString()});

        if (cursor == null) {

            return null;
        }

        T object = null;

        try {

            if (cursor.moveToFirst()) {

                object = getObjectFromCursor(clazz, cursor);
            }
        } finally {

            cursor.close();
        }
        return object;
    }

    public static <U extends BaseModel> List<U> getAllObjects(Class<U> clazz) {

        Cursor c = getAllObjectsInCursor(clazz);
        return getListFromCursor(clazz, c);
    }

    public static <U extends BaseModel> Cursor getAllObjectsInCursor(Class<U> clazz) {

        return getSQLiteDatabase().rawQuery("SELECT *, rowid _id FROM " + getTableName(clazz), null);
    }

    public static <T extends BaseModel> List<T> getByQuery(Class<T> clazz, String whereClause) {

        Cursor cursor = getByQueryInCursor(clazz, whereClause);
        return getListFromCursor(clazz, cursor);
    }

    public static <T extends BaseModel> Cursor getByQueryInCursor(Class<T> clazz, String whereClause) {

        String selectQuery = "SELECT *, rowid _id FROM " + getTableName(clazz) + " WHERE " + whereClause;
        return getSQLiteDatabase().rawQuery(selectQuery, null);
    }

    public static <T extends BaseModel> int getCountByQuery(Class<T> clazz, String whereClause) {

        String selectQuery = "SELECT COUNT(1) FROM " + getTableName(clazz) + " WHERE " + whereClause;
        Cursor c = getSQLiteDatabase().rawQuery(selectQuery, null);

        if (c == null) {

            return 0;
        }

        int size;
        try {

            size = c.moveToFirst() ? c.getInt(0) : 0;
        } finally {

            c.close();
        }


        return size;
    }

    protected ContentValues getContentValuesFromObject() {

        ContentValues cv = new ContentValues();

        Date now = new Date();

        for (Field field : getCachedFields(getClass())) {

            DataTypeEnum dt = getCachedDataType(this.getClass(), field);
            if (dt != null) {

                switch (dt) {

                    case LONG:
                        cv.put(field.getName(), (Long) getValueFromField(field));
                        break;
                    case BLOB:
                        cv.put(field.getName(), (byte[]) getValueFromField(field));
                        break;
                    case INTEGER:
                        cv.put(field.getName(), (Integer) getValueFromField(field));
                        break;
                    case ID_LONG:
                        LongId idLong = (LongId) getValueFromField(field);
                        if (idLong != null) {

                            cv.put(field.getName(), idLong.getId());
                        }
                        break;
                    case TEXT:
                        cv.put(field.getName(), (String) getValueFromField(field));
                        break;
                    case ID_TEXT:
                        UUIDId idUUID = (UUIDId) getValueFromField(field);
                        if (idUUID != null) {

                            cv.put(field.getName(), idUUID.getId());
                        }
                        break;
                    case DOUBLE:
                        cv.put(field.getName(), (Double) getValueFromField(field));
                        break;
                    case FLOAT:
                        cv.put(field.getName(), (Float) getValueFromField(field));
                        break;
                    case BOOLEAN:
                        cv.put(field.getName(), (Boolean) getValueFromField(field));
                        break;
                    case DATE:
                        Date date = (Date) getValueFromField(field);
                        if (date != null) {

                            cv.put(field.getName(), date.getTime());
                        }
                        break;
                    case ENUM:
                        Enum _enum = (Enum) getValueFromField(field);
                        if (_enum != null) {

                            cv.put(field.getName(), _enum.ordinal());
                        } else {

                            cv.put(field.getName(), -1);
                        }
                        break;
                }
            }

            SimpleDaoSystemFieldsEnum sdsfe = getCachedInternalField(this.getClass(), field);
            if (sdsfe != null) {

                BaseDateModel bdm;

                switch (sdsfe) {

                    case CREATE:

                        bdm = (BaseDateModel) this;
                        if (bdm.creationDate == null) {

                            cv.put(sdsfe.getName(), now.getTime());
                            bdm.creationDate = now;
                        } else {

                            cv.put(sdsfe.getName(), bdm.creationDate.getTime());
                        }
                        break;
                    case MODIFY:

                        bdm = (BaseDateModel) this;

                        if (bdm.updateModifiedDate) {

                            cv.put(sdsfe.getName(), now.getTime());
                            bdm.modifiedDate = now;
                        }
                        break;
                    case ID:
                        BaseModel bm = (BaseModel) this;

                        Id id;
                        // pokud jeste neni idcko, pak se jedna o novy objekt
                        if (bm.id == null) {
                            switch (getIdType((this).getClass())) {

                                case LONG:
                                    id = new LongId(0l);
                                    try {
                                        field.set(this, new LongId((Long) id.getId()));
                                    } catch (IllegalAccessException e) {

                                        throw new SimpleDroidDaoException("cannot set field id");
                                    }
                                    break;
                                case UUID:
                                    id = new UUIDId();
                                    cv.put(sdsfe.getName(), (String) id.getId());
                                    bm.id = id;
                                    ((UUIDId) bm.id).create = true;
                                    break;
                                default:
                                    throw new SimpleDroidDaoException("you shouldnt be here");
                            }
                        } else {

                            id = bm.id;
                            switch (getIdType((this).getClass())) {

                                case LONG:
                                    cv.put(sdsfe.getName(), (Long) id.getId());
                                    break;
                                case UUID:
                                    cv.put(sdsfe.getName(), (String) id.getId());
                                    ((UUIDId) id).create = ((UUIDId) id).manuallySet ? true : false;
                                    break;
                                default:
                                    throw new SimpleDroidDaoException("you shouldnt be here");
                            }
                        }

                }
            }
        }
        return cv;
    }

    public static <T extends BaseModel> T getObjectFromCursor(Class<T> clazz, Cursor cursor) {

        T instance = null;

        try {

            instance = clazz.newInstance();
        } catch (InstantiationException ie) {

            throw new SimpleDroidDaoException(ie);

        } catch (IllegalAccessException e) {

            throw new SimpleDroidDaoException(e);
        }

        for (Field field : getCachedFields(clazz)) {

            int columnIndex = cursor.getColumnIndex(field.getName());
            DataTypeEnum dt = getCachedDataType(clazz, field);
            if (dt != null) {

                try {
                    switch (dt) {

                        case LONG:
                            field.set(instance, cursor.getLong(columnIndex));
                            break;
                        case BLOB:
                            field.set(instance, cursor.getBlob(columnIndex));
                            break;
                        case DOUBLE:
                            field.set(instance, cursor.getDouble(columnIndex));
                            break;
                        case FLOAT:
                            field.set(instance, cursor.getFloat(columnIndex));
                            break;
                        case INTEGER:
                            field.set(instance, cursor.getInt(columnIndex));
                            break;
                        case BOOLEAN:
                            field.set(instance, convertToBoolean(cursor.getInt(columnIndex)));
                            break;
                        case TEXT:
                            field.set(instance, cursor.getString(columnIndex));
                            break;
                        case DATE:
                            field.set(instance, new Date(cursor.getLong(columnIndex)));
                            break;
                        case ID_LONG:
                            field.set(instance, new LongId(cursor.getLong(columnIndex)));
                            break;
                        case ID_TEXT:
                            field.set(instance, new UUIDId(cursor.getString(columnIndex)));
                            break;
                        case ENUM:
                            int i = cursor.getInt(columnIndex);

                            if (i != -1) {

                                field.set(instance, getCachedEnums(clazz, field)[i]);
                            } else {

                                field.set(instance, null);
                            }
                            break;
                    }
                } catch (IllegalAccessException e) {

                    throw new SimpleDroidDaoException(e);
                }
            }

            SimpleDaoSystemFieldsEnum sdsfe = getCachedInternalField(clazz, field);
            if (sdsfe != null) {

                try {
                    switch (sdsfe) {

                        case CREATE:
                        case MODIFY:

                            sDate.setTime(cursor.getLong(columnIndex));
                            field.set(instance, sDate.clone());
                            break;
                        case ID:

                            // pokud jeste neni idcko, pak se jedna o novy objekt
                            switch (getIdType((instance).getClass())) {

                                case LONG:
                                    LongId longId = new LongId(cursor.getLong(columnIndex));
                                    longId.mPrimaryKey = true;
                                    field.set(instance, longId);
                                    break;
                                case UUID:
                                    UUIDId uuidId = new UUIDId(cursor.getString(columnIndex));
                                    uuidId.mPrimaryKey = true;
                                    field.set(instance, uuidId);
                                    break;
                                default:
                                    throw new IllegalStateException("you shouldnt be here");
                            }
                            break;
                    }
                } catch (IllegalAccessException e) {

                    throw new SimpleDroidDaoException(e);
                }
            }
        }

        return instance;
    }

    private static Object[] getCachedEnums(Class clazz, Field field) {

        String name = clazz.getName().concat(field.getName());

        Object[] objects = sEnumCache.get(name);

        if (objects == null) {

            Class c = field.getType();
            objects = c.getEnumConstants();
            sEnumCache.put(name, objects);
        }

        return objects;
    }

    private Object getValueFromField(Field f) {

        Object value;
        try {

            value = f.get(this);
        } catch (IllegalAccessException e) {

            throw new SimpleDroidDaoException("field is not accessible");
        }

        return value;
    }

    private static List<Field> getCachedFields(Class clazz) {

        List<Field> list = sFieldsCache.get(clazz);

        if (list == null) {

            list = new ArrayList<Field>();

            list.addAll(Arrays.asList(clazz.getFields()));
            sFieldsCache.put(clazz, list);
        }

        return list;
    }

    private static DataTypeEnum getCachedDataType(Class clazz, Field field) {

        String name = clazz.getName().concat(field.getName());
        DataTypeEnum dte = sDataTypeCache.get(name);
        if (dte == null) {

            DataType dt = field.getAnnotation(DataType.class);

            if (dt != null) {

                dte = dt.type();
            } else if (field.getAnnotation(ForeignKey.class) != null) {

                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

                IdTypeEnum id = getIdType(foreignKey.attributeClass());

                switch (id) {

                    case LONG:

                        dte = DataTypeEnum.ID_LONG;
                        break;
                    case UUID:

                        dte = DataTypeEnum.ID_TEXT;
                        break;
                }

            } else {

                dte = DataTypeEnum.NONE;
            }

            sDataTypeCache.put(name, dte);
        }
        return dte;
    }

    private static SimpleDaoSystemFieldsEnum getCachedInternalField(Class clazz, Field field) {

        String name = clazz.getName().concat(field.getName());
        SimpleDaoSystemFieldsEnum sdsfe = sInternalFieldCache.get(name);
        if (sdsfe == null) {

            InternalFieldType ift = field.getAnnotation(InternalFieldType.class);

            if (ift != null) {

                sdsfe = ift.type();
                sInternalFieldCache.put(name, sdsfe);
            } else {

                sInternalFieldCache.put(name, null);
            }
        }
        return sdsfe;
    }

    static <T extends BaseModel> String getCreateTableSQL(Class<T> clazz) {

        CreateTableSqlBuilder ctsb = new CreateTableSqlBuilder(getTableName(clazz));
        for (Field field : getCachedFields(clazz)) {

            DataTypeEnum dt = getCachedDataType(clazz, field);

            if (dt != null) {

                NotNull notNull = field.getAnnotation(NotNull.class);
                Unique unique = field.getAnnotation(Unique.class);
                Index index = field.getAnnotation(Index.class);


                switch (dt) {

                    case BLOB:
                        ctsb.addBlobColumn(field.getName(), notNull != null);
                        break;
                    case DATE:
                    case INTEGER:
                    case BOOLEAN:
                    case LONG:
                    case ENUM:
                        ctsb.addIntegerColumn(field.getName(), notNull != null);
                        break;
                    case DOUBLE:
                    case FLOAT:
                        ctsb.addRealColumn(field.getName(), notNull != null);
                        break;
                    case TEXT:
                        ctsb.addTextColumn(field.getName(), notNull != null);
                        break;

                }

                if (unique != null) {

                    ctsb.addUniqueConstrain(field.getName());
                }

                if (index != null) {

                    ctsb.addSimpleIndex(field.getName());
                }


            }

            SimpleDaoSystemFieldsEnum sdsfe = getCachedInternalField(clazz, field);
            if (sdsfe != null) {
                switch (sdsfe) {

                    case CREATE:
                    case MODIFY:
                        ctsb.addIntegerColumn(field.getName());
                        break;
                    case ID:

                        switch (getIdType(clazz)) {

                            case LONG:
                                ctsb.addIntegerPrimaryColumn(field.getName());
                                break;
                            case UUID:
                                ctsb.addTextPrimaryColumn(field.getName());
                                break;
                        }
                }
            }

            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

            if (foreignKey != null) {

                String tableName = getTableName(foreignKey.attributeClass());

                if (field.getType() != Id.class) {

                    throw new SimpleDroidDaoException("with ForeignKey annotation you have to use Id data type");
                }

                IdTypeEnum id = getIdType(foreignKey.attributeClass());

                switch (id) {

                    case LONG:
                        ctsb.addIntegerColumn(field.getName(), true);
                        break;
                    case UUID:
                        ctsb.addTextColumn(field.getName(), true);
                        break;
                }

                ctsb.addSimpleIndex(field.getName());
                ctsb.addForeignKey(field.getName(), tableName, SimpleDaoSystemFieldsEnum.ID.getName(), foreignKey.deleteOnCascade(), foreignKey.updateOnCascade());
            }
        }
        String sql = ctsb.create();
        Log.i("SQL create statement", sql);
        return sql;
    }

    public static SQLiteDatabase getSQLiteDatabase() {

        return SimpleDroidDao.getOpenedDatabase();
    }

    public static <T extends BaseModel> Map<Id, T> getAllObjectsAsMap(Class<T> clazz) {

        Map<Id, T> map = new HashMap<Id, T>();
        List<T> objects = getAllObjects(clazz);
        for (T t : objects) {

            map.put(t.id, t);
        }

        return map;
    }

    public T save() {

        return save(false);
    }

    public T save(boolean fastWithoutNotification) {

        // namapuji objekt na db objekt
        ContentValues cv = getContentValuesFromObject();

        T object = (T) this;

        try {
            // pokud nema objekt vyplnene id, pak vytvarime novy zaznam do DB
            if (((AbstractId) object.id).operationType() == AbstractId.OperationType.CREATE) {

                // insertujem do db
                long id = getSQLiteDatabase().insertOrThrow(getTableName(object.getClass()), null, cv);

                // pokud nedoslo k chybe
                if (id != -1) {

                    // vratime vygenerovane id
                    if (object.id instanceof LongId) {

                        object.id = new LongId(id);
                    }

                    if (!(fastWithoutNotification || excludeFromNotifier(object.getClass()))) {

                        mEventMonitor.notifyOnCreateObject(object);
                    }
                    return object;
                } else {

                    // jinak vyhodime runtime exception
                    throw new SimpleDroidDaoException("insert failed");
                }

            } else {

                // pokud jiz existuje id, pak provedem update
                long updatedID = getSQLiteDatabase().update(getTableName(object.getClass()), cv, SimpleDaoSystemFieldsEnum.ID.getName() + "='" + object.id + "'", null);
                // pokud update probehl v poradku
                if (updatedID > 0) {

                    if (!(fastWithoutNotification || excludeFromNotifier(object.getClass()))) {

                        mEventMonitor.notifyOnUpdateObject(object);
                    }
                    // vratime vygenerovane id
                    return object;
                } else {

                    // jinak vyhodime runtime exception
                    throw new SimpleDroidDaoException("update failed for object id " + object.id);
                }
            }
        } catch (SQLiteConstraintException sce) {

            throw ConstraintExceptionFactory.getException(sce);
        }
    }

    protected static <T extends BaseModel> List<T> getListFromCursor(Class<T> clazz, Cursor cursor) {

        List<T> list = new ArrayList<T>();
        // pokud je cursor nullovy, pak vratime prazdny seznam
        if (cursor == null) {

            return list;
        }
        try {

            // pokud v kurzoru je alespon jeden zaznam
            if (cursor.moveToFirst()) {

                // prochazej pres vsechny zaznamy v cursoru dokud muzes
                do {

                    // vytvor objekt
                    T object = getObjectFromCursor(clazz, cursor);

                    // a pridej ho do seznamu
                    list.add(object);

                } while (cursor.moveToNext());
            }
        } finally {

            // uzavreme cursor
            cursor.close();
        }

        return list;
    }

    static <T extends BaseModel> void createTable(Class<T> clazz) {

        getSQLiteDatabase().execSQL(getCreateTableSQL(clazz));
    }

    static <T extends BaseModel> void dropTable(Class<T> clazz) {

        getSQLiteDatabase().execSQL("drop table if exists " + getTableName(clazz));
    }

    public void delete() throws DaoConstraintException {

        BaseModel bm = (BaseModel) this;
        try {

            getSQLiteDatabase().delete(getTableName(getClass()), SimpleDaoSystemFieldsEnum.ID + "=?",
                    new String[]{bm.id.toString()});
        } catch (SQLiteConstraintException sce) {

            throw new DaoConstraintException(DaoConstraintException.ConstraintsExceptionType.DELETE, sce);
        }

        if (!excludeFromNotifier(getClass())) {
            mEventMonitor.notifyOnDeleteObject(bm);
        }

        bm.id = null;
    }

    /**
     * Pomocna metoda pro konverzi z intu do booleanu
     */
    private static boolean convertToBoolean(int i) {

        return i != 0;

    }

    public static <T extends BaseModel> void deleteAll(Class<T> clazz) throws DaoConstraintException {

        deleteAll(clazz, false);
    }

    public static <T extends BaseModel> void deleteAll(Class<T> clazz, boolean fastWithoutNotification) throws DaoConstraintException {

        if (fastWithoutNotification) {

            try {

                getSQLiteDatabase().delete(getTableName(clazz), null, null);
            } catch (SQLiteConstraintException sce) {

                throw new DaoConstraintException(DaoConstraintException.ConstraintsExceptionType.DELETE, sce);
            }
        } else {

            List<T> allObjects = getAllObjects(clazz);
            for (T t : allObjects) {

                t.delete();
            }
        }
    }

    public static <T extends BaseModel> void deleteByQuery(Class<T> clazz, String whereClause) throws DaoConstraintException {

        deleteByQuery(clazz, whereClause, false);
    }

    public static <T extends BaseModel> void deleteByQuery(Class<T> clazz, String whereClause, boolean fastWithoutNotification) throws DaoConstraintException {

        if (fastWithoutNotification) {

            try {

                getSQLiteDatabase().delete(getTableName(clazz), whereClause, null);
            } catch (SQLiteConstraintException sce) {

                throw new DaoConstraintException(DaoConstraintException.ConstraintsExceptionType.DELETE, sce);
            }
        } else {

            List<T> list = getByQuery(clazz, whereClause);
            for (T t : list) {

                t.delete();
            }
        }
    }

    public static <T extends BaseModel> String getTableName(Class<?> clazz) {

        String tableName = sTableNameMap.get(clazz);

        if (tableName == null) {

            TableName tn = clazz.getAnnotation(TableName.class);

            if (tn == null) {

                throw new IllegalStateException("no table name annotation defined!");
            }

            tableName = tn.name();
            sTableNameMap.put(clazz, tableName);
        }
        return tableName;
    }

    private static boolean excludeFromNotifier(Class<?> clazz) {

        Boolean exclude = sExcludeFromNtfNameMap.get(clazz);
        if (exclude == null) {

            exclude = clazz.isAnnotationPresent(ExcludeFromNotifiers.class);
            sExcludeFromNtfNameMap.put(clazz, exclude);
        }
        return exclude;
    }


    private static <T extends BaseModel> IdTypeEnum getIdType(Class<?> clazz) {

        IdTypeEnum typeEnum = sIdTypeEnumMap.get(clazz);

        if (typeEnum == null) {

            IdType idType = clazz.getAnnotation(IdType.class);
            if (idType == null) {

                throw new IllegalStateException("no id type annotation defined");
            }

            typeEnum = idType.type();
            sIdTypeEnumMap.put(clazz, typeEnum);
        }
        return typeEnum;
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface TableName {

        String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface Entity {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface ExcludeFromNotifiers {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface IdType {

        IdTypeEnum type();
    }

    public enum IdTypeEnum {
        LONG,
        UUID
    }

    public enum DataTypeEnum {

        INTEGER,
        LONG,
        DOUBLE,
        FLOAT,
        TEXT,
        BLOB,
        DATE,
        BOOLEAN,
        ENUM,
        ID_TEXT,
        ID_LONG,
        NONE
    }

    public enum SimpleDaoSystemFieldsEnum {

        ID("id"),
        MODIFY("modifiedDate"),
        CREATE("creationDate");

        private String name;

        private SimpleDaoSystemFieldsEnum(String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface DataType {

        DataTypeEnum type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface InternalFieldType {

        SimpleDaoSystemFieldsEnum type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Index {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Unique {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface NotNull {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface ForeignKey {

        boolean deleteOnCascade() default false;

        boolean updateOnCascade() default false;

        Class attributeClass();
    }


}

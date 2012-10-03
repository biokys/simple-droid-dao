package eu.janmuller.android.dao.api;


import eu.janmuller.android.dao.CreateTableSqlBuilderExtended;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:48
 */
public abstract class GenericModel<T> implements ISimpleDroidDao<T> {

    public static int getCount() {

        return 0;
    }

    public static <U extends BaseModel> U findObjectById(Id id) {

        return (U) new Foo();
    }

    public static <U extends BaseModel> List<U> getAllObjects() {

        List<U> list = new ArrayList<U>();
        return list;
    }

    private Map<String, Object> getObject2DbMapping() {

        Map<String, Object> cv = new HashMap<String, Object>();
        //ContentValues cv = new ContentValues();

        Date now = new Date();

        for (Field field : getClass().getFields()) {

            DataType dt = field.getAnnotation(DataType.class);
            if (dt != null) {

                switch (dt.type()) {

                    case BLOB:
                        cv.put(field.getName(), (byte[]) getValueFromField(field));
                        break;
                    case DATE:
                        cv.put(field.getName(), ((Date) getValueFromField(field)).getTime());
                        break;
                    case DOUBLE:
                        cv.put(field.getName(), (Double) getValueFromField(field));
                        break;
                    case ENUM:
                        cv.put(field.getName(), ((Enum) getValueFromField(field)).ordinal());
                        break;
                    case INTEGER:
                        cv.put(field.getName(), (Integer) getValueFromField(field));
                        break;
                    case TEXT:

                        cv.put(field.getName(), (String) getValueFromField(field));
                        break;

                }
            }

            InternalFieldType ift = field.getAnnotation(InternalFieldType.class);
            if (ift != null) {
                switch (ift.type()) {

                    case CREATE:

                        BaseDateModel bdm = (BaseDateModel) this;
                        if (bdm.creationDate == null) {

                            cv.put(ift.type().getName(), now);
                        }
                        break;
                    case MODIFY:
                        cv.put(ift.type().getName(), now);

                        break;
                    case ID:
                        BaseModel bm = (BaseModel) this;

                        // pokud jeste neni idcko, pak se jedna o novy objekt
                        if (bm.id == null) {
                            switch (getIdType()) {

                                case LONG:
                                    break;
                                case STRING:
                                    break;
                                case UUID:
                                    break;
                            }
                        }
                        cv.put(ift.type().getName(), now);
                        break;
                }
            }


        }


        return cv;
    }

    private T getObjectFromContentValues(Map<String, Object> cv) {


        T instance = null;
        try {
            instance = (T) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();

            for (Field field : instance.getClass().getFields()) {

                DataType dt = field.getAnnotation(DataType.class);
                if (dt != null) {

                    switch (dt.type()) {

                        case BLOB:

                            field.set(instance, cv.get(field.getName()));
                            break;
                        case DATE:
                            field.set(instance, new Date((Long) cv.get(field.getName())));
                            break;
                        case DOUBLE:
                            field.set(instance, cv.get(field.getName()));
                            break;
                        case ENUM:
                            int i = (Integer) cv.get(field.getName());
                            Class c = field.getType();
                            field.set(instance, c.getFields()[i].get(instance));
                            break;
                        case INTEGER:
                            field.set(instance, cv.get(field.getName()));
                            break;
                        case TEXT:
                            field.set(instance, cv.get(field.getName()));
                            break;

                    }
                }
            }
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        }

        return instance;
    }

    private Object getValueFromField(Field f) {

        Object value;
        try {

            value = f.get(this);
        } catch (IllegalAccessException e) {

            throw new IllegalStateException("field is not accessible");
        }

        if (value == null) {

            throw new IllegalStateException("value is null!");
        }

        return value;
    }

    private String getCreateTableSQL() {


        CreateTableSqlBuilderExtended ctsb = new CreateTableSqlBuilderExtended(getTableName());
        for (Field field : getClass().getFields()) {

            DataType dt = field.getAnnotation(DataType.class);
            if (dt != null) {

                NotNull notNull = field.getAnnotation(NotNull.class);
                Unique unique = field.getAnnotation(Unique.class);
                Index index = field.getAnnotation(Index.class);


                switch (dt.type()) {

                    case BLOB:
                        ctsb.addBlobColumn(field.getName(), notNull != null);
                        break;
                    case DATE:
                        ctsb.addIntegerColumn(field.getName(), notNull != null);
                        break;
                    case DOUBLE:
                        ctsb.addRealColumn(field.getName(), notNull != null);
                        break;
                    case ENUM:
                        ctsb.addIntegerColumn(field.getName(), notNull != null);
                        break;
                    case INTEGER:
                        ctsb.addIntegerColumn(field.getName(), notNull != null);
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

            InternalFieldType ift = field.getAnnotation(InternalFieldType.class);
            if (ift != null) {
                switch (ift.type()) {

                    case CREATE:
                    case MODIFY:
                        ctsb.addIntegerColumn(field.getName());
                        break;
                    case ID:

                        switch (getIdType()) {

                            case LONG:
                                ctsb.addIntegerPrimaryColumn(field.getName());
                                break;
                            case STRING:
                                ctsb.addTextPrimaryColumn(field.getName());
                                break;
                            case UUID:
                                ctsb.addTextPrimaryColumn(field.getName());
                                break;
                        }
                }
            }
        }

        return ctsb.create();
    }

    @Override
    public T save() {

        // namapuji objekt na db objekt
        Map<String, Object> map = getObject2DbMapping();

        T t = getObjectFromContentValues(map);

        String s = getCreateTableSQL();

        t.toString();

        /*boolean isUpdate = false;

        try {
            // pokud nema objekt vyplnene id, pak vytvarime novy zaznam do DB
            if (object.isNew()) {

                // insertujem do db
                long id = getSQLiteDatabase().insertOrThrow(getTableNameWithCheck(), null, cv);

                // pokud nedoslo k chybe
                if (id != -1) {

                    // vratime vygenerovane id
                    return cv.get(KEY_ID);
                } else {

                    // jinak vyhodime runtime exception
                    throw new RuntimeException("insert failed");
                }

            } else {

                // pokud jiz existuje id, pak provedem update
                long updatedID = getSQLiteDatabase().update(getTableNameWithCheck(), cv, KEY_ID + "='" + object.getId() + "'", null);

                isUpdate = true;

                // pokud update probehl v poradku
                if (updatedID > 0) {

                    // vratime vygenerovane id
                    return object.getId();
                } else {

                    // jinak vyhodime runtime exception
                    throw new RuntimeException("update failed for object id " + object.getId());
                }
            }
        } catch (SQLiteConstraintException sce) {


            throw new DaoConstraintException(isUpdate ? DaoConstraintException.ConstraintsExceptionType.UPDATE :
                    DaoConstraintException.ConstraintsExceptionType.INSERT, sce);
        }*/
        return null;
    }


    public T load() {

        return null;
    }

    public void delete() {

    }

    private String getTableName() {

        TableName tn = getClass().getAnnotation(TableName.class);

        if (tn == null) {

            throw new IllegalStateException("no table name annotation defined!");
        }
        return tn.name();
    }

    private IdTypeEnum getIdType() {

        IdType idType = getClass().getAnnotation(IdType.class);
        if (idType == null) {

            throw new IllegalStateException("no id type annotation defined");
        }
        return idType.type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface TableName {

        String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface IdType {

        IdTypeEnum type();
    }

    public enum IdTypeEnum {
        STRING,
        LONG,
        UUID
    }

    public enum DataTypeEnum {

        INTEGER,
        DOUBLE,
        TEXT,
        BLOB,
        DATE,
        ENUM
    }

    public enum SimpleDaoSystemFieldsEnum {

        ID("id"),
        MODIFY("modified"),
        CREATE("created");

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
    @interface InternalFieldType {

        SimpleDaoSystemFieldsEnum type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Index {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Unique {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface NotNull {
    }


}

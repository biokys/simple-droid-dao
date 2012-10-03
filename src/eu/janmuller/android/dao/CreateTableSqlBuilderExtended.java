package eu.janmuller.android.dao;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 19:17
 */
public class CreateTableSqlBuilderExtended extends CreateTableSqlBuilder {

    private CreateTableSqlBuilderExtended(String tableName, IDTypes idTypes) {
        super(tableName, idTypes);
    }

    public CreateTableSqlBuilderExtended(String tableName) {
        super(tableName);

        this.sql = "create table if not exists " + tableName + "(";
    }

    private String createPrimaryColumn(String dataType, String name) {

        return "," + name + " " + dataType + " not null primary key";
    }

    public CreateTableSqlBuilderExtended addIntegerPrimaryColumn(String name) {

        this.sql += createPrimaryColumn(DATA_TYPE_INTEGER, name);
        return this;
    }

    public CreateTableSqlBuilderExtended addTextPrimaryColumn(String name) {

        this.sql += createPrimaryColumn(DATA_TYPE_TEXT, name);
        return this;
    }

    @Override
    public String create() {

        return super.create();
    }
}

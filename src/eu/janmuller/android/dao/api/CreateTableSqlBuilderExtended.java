package eu.janmuller.android.dao.api;

import eu.janmuller.android.dao.api.CreateTableSqlBuilder;

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

    private void createPrimaryColumn(String dataType, String name, boolean autoincrement) {

        partialSqls.add(name + " " + dataType + " not null primary key" + (autoincrement ? " autoincrement " : " "));
    }

    public CreateTableSqlBuilderExtended addIntegerPrimaryColumn(String name) {

        createPrimaryColumn(DATA_TYPE_INTEGER, name, true);
        return this;
    }

    public CreateTableSqlBuilderExtended addTextPrimaryColumn(String name) {

        createPrimaryColumn(DATA_TYPE_TEXT, name, false);
        return this;
    }

    @Override
    public String create() {

        return super.create();
    }
}

package eu.janmuller.android.dao.api;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:48
 */
@GenericModel.TableName(name = "foo_table")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.STRING)
public class Foo extends BaseDateModel<Foo, String> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @DataType(type = DataTypeEnum.INTEGER)
    @Index
    public int age;

    @DataType(type = DataTypeEnum.DATE)
    public Date birthday;

    @DataType(type = DataTypeEnum.ENUM)
    public TestEnum tEnum;

    public int count;


}

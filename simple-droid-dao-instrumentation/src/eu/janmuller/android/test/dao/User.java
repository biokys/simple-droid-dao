package eu.janmuller.android.test.dao;

import eu.janmuller.android.dao.api.BaseDateModel;
import eu.janmuller.android.dao.api.GenericModel;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 07.11.12
 * Time: 16:08
 */
@GenericModel.Entity
@GenericModel.TableName(name = "user")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.LONG)
public class User extends BaseDateModel<User> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @DataType(type = DataTypeEnum.INTEGER)
    @Index
    public int age;

    @DataType(type = DataTypeEnum.DATE)
    public Date birthday;

    @DataType(type = DataTypeEnum.ENUM)
    public CountryEnum country;


}

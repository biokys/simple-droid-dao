package eu.janmuller.android.test.dao;

import eu.janmuller.android.dao.api.BaseDateModel;
import eu.janmuller.android.dao.api.GenericModel;
import eu.janmuller.android.dao.api.Id;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 07.11.12
 * Time: 16:08
 */
@GenericModel.Entity
@GenericModel.TableName(name = "userFK")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.LONG)
public class UserFK extends BaseDateModel<UserFK> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @ForeignKey(attributeClass = Customer.class, deleteOnCascade = true)
    public Id customerId;


}

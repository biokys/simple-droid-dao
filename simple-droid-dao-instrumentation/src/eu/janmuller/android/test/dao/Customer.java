package eu.janmuller.android.test.dao;

import eu.janmuller.android.dao.api.BaseDateModel;
import eu.janmuller.android.dao.api.GenericModel;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 14.11.12
 * Time: 14:53
 */
@GenericModel.Entity
@GenericModel.TableName(name = "customer")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.UUID)
public class Customer extends BaseDateModel<Customer> {

    @DataType(type = DataTypeEnum.TEXT)
    public String name;
}

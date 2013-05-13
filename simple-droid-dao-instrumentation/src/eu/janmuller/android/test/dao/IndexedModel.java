package eu.janmuller.android.test.dao;

import eu.janmuller.android.dao.api.BaseDateModel;
import eu.janmuller.android.dao.api.GenericModel;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 01.12.12
 * Time: 23:36
 */
@GenericModel.Entity
@GenericModel.TableName(name = "imodel")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.UUID)
public class IndexedModel extends BaseDateModel<IndexedModel> {

    @Index
    @DataType(type = DataTypeEnum.TEXT)
    public String name;

    @Index
    @DataType(type = DataTypeEnum.TEXT)
    public String email;

    @Index
    @DataType(type = DataTypeEnum.INTEGER)
    public int age;
}

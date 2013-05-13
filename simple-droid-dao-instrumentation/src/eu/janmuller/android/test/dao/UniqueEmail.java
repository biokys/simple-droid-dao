package eu.janmuller.android.test.dao;

import eu.janmuller.android.dao.api.BaseDateModel;
import eu.janmuller.android.dao.api.GenericModel;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 01.12.12
 * Time: 22:27
 */
@GenericModel.Entity
@GenericModel.TableName(name = "email")
@GenericModel.IdType(type = GenericModel.IdTypeEnum.UUID)
public class UniqueEmail extends BaseDateModel<UniqueEmail> {

    @Unique
    @NotNull
    @DataType(type = DataTypeEnum.TEXT)
    public String email;
}

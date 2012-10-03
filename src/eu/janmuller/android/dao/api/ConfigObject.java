package eu.janmuller.android.dao.api;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 14:50
 */
public class ConfigObject {

    Class<? extends Id> idType;

    Class<? extends BaseModel> modelType;

    public <T, U> ConfigObject(Class<? extends Id> idType, Class<? extends BaseModel> modelType) {

        this.idType = idType;
        this.modelType = modelType;
    }

    public Class<? extends Id> getIdType() {

        return idType;
    }

    public Class<? extends BaseModel> getModelType() {

        return modelType;
    }
}

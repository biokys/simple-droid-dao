package eu.janmuller.android.dao.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 30.12.12
 * Time: 18:40
 */
public class SimpleDroidDaoEventMonitor {

    private static final SimpleDroidDaoEventMonitor INSTANCE = new SimpleDroidDaoEventMonitor();

    private SimpleDroidDaoEventMonitor() {}

    public static SimpleDroidDaoEventMonitor getInstance() {

        return INSTANCE;
    }

    private List<IBaseEventListener> mBaseEventListeners = new ArrayList<IBaseEventListener>();

    public void registerApiEventListener(IBaseEventListener baseEventListener) {

        mBaseEventListeners.add(baseEventListener);
    }

    public void unregisterApiEventListener(IBaseEventListener baseEventListener) {

        mBaseEventListeners.remove(baseEventListener);
    }

    void notifyOnDeleteObject(BaseModel baseModel) {

        for (IBaseEventListener  baseEventListener : mBaseEventListeners) {

            if (baseEventListener instanceof IDeleteEventListener) {

                ((IDeleteEventListener) baseEventListener).onDelete(baseModel);
            }
        }
    }

    void notifyOnCreateObject(BaseModel baseModel) {

        for (IBaseEventListener  baseEventListener : mBaseEventListeners) {

            if (baseEventListener instanceof ISaveEventListener) {

                ((ISaveEventListener) baseEventListener).onSave(baseModel);
            }
        }
    }

    void notifyOnUpdateObject(BaseModel baseModel) {

        for (IBaseEventListener  baseEventListener : mBaseEventListeners) {

            if (baseEventListener instanceof ISaveEventListener) {

                ((IUpdateEventListener) baseEventListener).onUpdate(baseModel);
            }
        }
    }

    interface IBaseEventListener {
    }

    public interface IDeleteEventListener extends IBaseEventListener {

        public void onDelete(BaseModel baseModel);

    }

    public interface ISaveEventListener extends IBaseEventListener {

        public void onSave(BaseModel baseModel);

    }

    public interface IUpdateEventListener extends IBaseEventListener {

        public void onUpdate(BaseModel baseModel);

    }
}



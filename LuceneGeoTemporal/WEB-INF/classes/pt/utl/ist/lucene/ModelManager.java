package pt.utl.ist.lucene;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This class manages the model used in each thread by a search
 * @author Jorge Machado
 * @date 26/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class ModelManager
{

    private static ModelManager instance = new ModelManager();

    private ModelManager(){}

    public static ModelManager getInstance()
    {
        return instance;
    }

    private static long THREAD_TIMEOUT = 10000;

    private long lastCheck = 0;

    private HashMap<Long,ModelContainer> threadModels = new HashMap<Long,ModelContainer>();


    public void setModel(Model m)
    {
        service(ManagerService.setModel,m);
    }

    public void reset()
    {
        service(ManagerService.clear,null);
    }

    public Model getModel()
    {
        return service(ManagerService.getModel,null);
    }

//    public synchronized Model service(ManagerService service, Model m)
    public synchronized Model service(ManagerService service, Model m)
    {
        switch ( service )
        {
            case setModel: threadModels.put(Thread.currentThread().getId(),new ModelContainer(m)); break;
            case getModel: return threadModels.get(Thread.currentThread().getId()).getModel();
            case clear: threadModels.clear();
        }
        checkOldThreads();
        return null;
    }

    private synchronized void checkOldThreads()
    {
        if(System.currentTimeMillis() - lastCheck > THREAD_TIMEOUT)
        {
            lastCheck = System.currentTimeMillis();
            List<Long> toRemoveKey = new ArrayList<Long>();
            for(Map.Entry<Long, ModelContainer> entry: threadModels.entrySet())
            {
                if(System.currentTimeMillis() - entry.getValue().getLastAccess() > THREAD_TIMEOUT)
                {
                    toRemoveKey.add(entry.getKey());
                }
            }
            for(Long key:toRemoveKey)
            {
                threadModels.remove(key);
            }
        }
    }

    private enum ManagerService
    {
        setModel,
        clear,
        getModel
    }

    private class ModelContainer
    {
        Model m;
        long lastAccess = 0;


        public ModelContainer(Model m)
        {
            lastAccess = System.currentTimeMillis();
            this.m = m;
        }

        public Model getModel()
        {
            lastAccess = System.currentTimeMillis();
            return m;
        }

        public long getLastAccess()
        {
            return lastAccess;
        }
    }


}

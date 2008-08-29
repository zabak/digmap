package pt.utl.ist.lucene;

import java.util.HashMap;
import java.util.Map;

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
        ModelContainer mc = threadModels.get(Thread.currentThread().getId());
        if(mc != null)
            return mc.getModel();
        return null;
    }

//    public synchronized Model service(ManagerService service, Model m)
    public Model service(ManagerService service, Model m)
    {
        switch ( service )
        {
            case setModel: threadModels.put(Thread.currentThread().getId(),new ModelContainer(m)); break;
            case clear: threadModels.clear();
        }
        checkOldThreads();
        return null;
    }

    private void checkOldThreads()
    {
        if(System.currentTimeMillis() - lastCheck > THREAD_TIMEOUT)
        {
            lastCheck = System.currentTimeMillis();
            for(Map.Entry<Long, ModelContainer> entry: threadModels.entrySet())
            {
                if(System.currentTimeMillis() - entry.getValue().getLastAccess() > THREAD_TIMEOUT)
                {
                    threadModels.remove(entry.getKey());
                }
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

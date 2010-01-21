package pt.utl.ist.lucene;

import java.util.*;

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

    private static long THREAD_TIMEOUT = 600000;

    private long lastCheck = 0;

    private HashMap<Long,ModelContainer> threadModels = new HashMap<Long,ModelContainer>();


    public void setModel(Model m)
    {
        service(ManagerService.setModel,m);
    }

    public void setModel(Model m, Properties modelProperties)
    {
        service(ManagerService.setModel,m);
        service(ManagerService.setModelProperties,modelProperties);
    }

    public void setModel(Model m, QueryConfiguration queryConfiguration)
    {
        service(ManagerService.setModel,m);
        service(ManagerService.setQueryConfiguration,queryConfiguration);
    }

    public void setQueryConfiguration(QueryConfiguration queryConfiguration)
    {
        service(ManagerService.setQueryConfiguration,queryConfiguration);
    }



    public void reset()
    {
        service(ManagerService.clear,null);
    }

    public Model getModel()
    {
        return (Model) service(ManagerService.getModel,null);
    }

    public QueryConfiguration getQueryConfiguration()
    {
        QueryConfiguration queryConfiguration = (QueryConfiguration) service(ManagerService.getQueryConfiguration,null);
        if(queryConfiguration == null)
        {
            queryConfiguration = new QueryConfiguration();
            service(ManagerService.setQueryConfiguration,queryConfiguration);
        }
        return queryConfiguration;
    }

    public boolean hasQueryConfiguration()
    {
        QueryConfiguration queryConfiguration = (QueryConfiguration) service(ManagerService.getQueryConfiguration,null);
        return queryConfiguration != null;
    }

     public boolean hasModel()
    {
        Model model = (Model) service(ManagerService.getModel,null);
        return model != null;
    }

     public boolean hasModelProperties()
    {
        Properties modelProperties = (Properties) service(ManagerService.getModelProperties,null);
        return modelProperties != null;
    }

    public Properties getModelProperties()
    {
        return (Properties) service(ManagerService.getModelProperties,null);
    }

    //    public synchronized Model service(ManagerService service, Model m)
    public synchronized Object service(ManagerService service, Object o)
    {
        switch ( service )
        {
            case setModel: threadModels.put(Thread.currentThread().getId(),new ModelContainer((Model) o)); break;

            case setQueryConfiguration:{
                if(threadModels.get(Thread.currentThread().getId())!=null)
                    threadModels.get(Thread.currentThread().getId()).setQueryConfiguration((QueryConfiguration) o);
                else
                    threadModels.put(Thread.currentThread().getId(),new ModelContainer(Model.defaultModel, (QueryConfiguration) o));
                break;
            }
            case setModelProperties:{
                if(threadModels.get(Thread.currentThread().getId())!=null)
                    threadModels.get(Thread.currentThread().getId()).setModelProperties((Properties) o);
                else
                    threadModels.put(Thread.currentThread().getId(),new ModelContainer(Model.defaultModel, (Properties) o));
                break;
            }
            case getModel: return threadModels.get(Thread.currentThread().getId()).getModel();
            case getQueryConfiguration: return threadModels.get(Thread.currentThread().getId()).getQueryConfiguration();
            case getModelProperties: return threadModels.get(Thread.currentThread().getId()).getModelProperties();
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
        setQueryConfiguration,
        getModelProperties,
        setModelProperties,
        clear,
        getModel,
        getQueryConfiguration
    }

    private class ModelContainer
    {
        Model m;
        long lastAccess = 0;
        QueryConfiguration queryConfiguration;
        Properties modelProperties = null;


        public ModelContainer(Model m)
        {
            lastAccess = System.currentTimeMillis();
            this.m = m;
        }

        public ModelContainer(Model m, Properties modelProperties)
        {
            this.modelProperties = modelProperties;
            lastAccess = System.currentTimeMillis();
            this.m = m;
        }

        public ModelContainer(Model m, QueryConfiguration queryConfiguration)
        {
            lastAccess = System.currentTimeMillis();
            this.m = m;
            this.queryConfiguration = queryConfiguration;
        }

        public ModelContainer(Model m, QueryConfiguration queryConfiguration, Properties modelProperties)
        {
            lastAccess = System.currentTimeMillis();
            this.m = m;
            this.queryConfiguration = queryConfiguration;
            this.modelProperties = modelProperties;
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


        public Properties getModelProperties() {
            return modelProperties;
        }

        public void setModelProperties(Properties modelProperties) {
            this.modelProperties = modelProperties;
        }

        public QueryConfiguration getQueryConfiguration() {
            return queryConfiguration;
        }

        public void setQueryConfiguration(QueryConfiguration queryConfiguration) {
            this.queryConfiguration = queryConfiguration;
        }
    }


}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author Bruno Martins
 */
public class BuildIndex {

    private Environment env;
    private Database db[] = new Database[6];
    private Map<String, Long> map[] = new Map[6];
    
	private static final String CONFIG_FILE = "wet-1t-ngram.properties";
	private static final String DEFAULT_CONFIG_FILE = "default.properties";
	
	private String datasetPath;

    public static void main(String[] argv) throws Exception {
        String dir = getProperties().getProperty("index.path");
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(false);
        envConfig.setAllowCreate(true);
        File root = new File(dir);
        root.mkdirs();
        Environment env = new Environment(root, envConfig);
        BuildIndex worker = new BuildIndex(env,"true".equals(getProperties().getProperty("index.clear.existing")));
        try {
    		worker.datasetPath = getProperties().getProperty("ngram.path");
    		if(worker.datasetPath != null && worker.datasetPath.length()>0) worker.doWork();
        	for ( int i = 0; i < 5; i++ ) {
        		worker.datasetPath = getProperties().getProperty("ngram.path." + ( i+1 ));
        		if(worker.datasetPath != null && worker.datasetPath.length()>0) worker.doWork();
        		System.out.println("ngram.size." + (i+1) + " : " + worker.map[i].size());
        	}
        } finally {
            worker.close();
        }
    	System.out.println("Testing JSON output for an example query... ");
    	System.out.println();
    	SearchNGrams aux = new SearchNGrams();
    	System.out.println(aux.getNGramFrequency("one two three","json"));
    	System.out.println();
    }

    private BuildIndex(Environment env, boolean eraseExistingData) throws Exception {
        this.env = env;
        open(eraseExistingData);
    }

    public void doWork() throws Exception {
    	doWork(datasetPath);
    }
    
    private void doWork(String datasetPath) throws Exception {
    	File in[] = { new File(datasetPath) };
    	for ( File f : ( in[0].isFile() ? in : in[0].listFiles())) if(f.isFile()) {
    		// Skip the ".idx" files from the Google Dataset distribution
    		if (f.getPath().endsWith(".idx")) continue;
    		System.out.print("Adding n-gram information from " + f.getAbsolutePath());
    		System.out.print(" ...");
    		BufferedReader reader = isGZip(f) ? new BufferedReader( new InputStreamReader( new GZIPInputStream( new FileInputStream(f) ) ) ) : new BufferedReader( new FileReader(f) );
        	String dline;
        	int numLines = 0;
        	while ( (dline = reader.readLine()) != null ) {
        		String  key = dline.substring(0,dline.lastIndexOf("\t"));
        		Long    val = new Long(dline.substring(dline.lastIndexOf("\t")+1));
            	int n = key.split(" +").length-1;
        		map[n>5 ? 5 :n].put(key,val);
        		if((++numLines % 100000) == 0) System.out.print(".");
        	}
        	System.out.println(".");
        } else doWork(f.getAbsolutePath());        
    }

    private void open(boolean eraseExistingData ) throws Exception {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(false);
        dbConfig.setAllowCreate(true);
        TupleBinding<String> keyBinding = TupleBinding.getPrimitiveBinding(String.class);
        TupleBinding<Long> dataBinding = TupleBinding.getPrimitiveBinding(Long.class);
        for (int i=0; i<6; i++) {
          if(eraseExistingData) try {
        	  env.removeDatabase(null, ((i+1) + ".gram"));
          } catch ( Exception e ) { }
          this.db[i] = env.openDatabase(null, ((i+1) + ".gram"), dbConfig);
          this.map[i] = new StoredMap<String, Long>(db[i], keyBinding, dataBinding, true);
		}
    }

    private void close() throws Exception {
        for (int i=0; i<6; i++) {
        if (db[i] != null) {
            db[i].close();
            db[i] = null;
        }
		}
        if (env != null) {
            env.close();
            env = null;
        }
    }

    public boolean isGZip ( File f ) {
    	try {
    		return new GZIPInputStream( new FileInputStream(f)).available() >= 1;
    	} catch ( Exception e ) {
    		return false;
    	}
    }
	
	private static void loadPropertyFromStream(Properties props, InputStream in) throws IOException {
		try {
			if (in!=null) {
				props.load(in);
			}
		} finally {
			if (in!=null) {
				in.close();
			}
		}
	}
	
	protected static Properties getProperties() {
		Properties props = new Properties();
		try {
			loadPropertyFromStream(props,BuildIndex.class.getResourceAsStream(DEFAULT_CONFIG_FILE));
			loadPropertyFromStream(props,Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));
			File f = new File(CONFIG_FILE);
			if (f.exists()) loadPropertyFromStream(props, new FileInputStream(f));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		return props;
	}

}

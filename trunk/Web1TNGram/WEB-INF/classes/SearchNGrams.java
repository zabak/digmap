import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author Bruno Martins
 */
public class SearchNGrams implements TransactionWorker {

    private Environment env;
    private Database db[] = new Database[6];
    private Map<String, Long> map[] = new Map[6];

	private String data;
	private int code;
	
    public static void main(String[] args) throws Exception {
    	SearchNGrams ngrams = new SearchNGrams();
    	System.out.println( "" + ngrams.getNGramFrequency(args) );
    }
	
    public SearchNGrams ( ) {
    	this(getProperties().getProperty("index.path"));
    }
    
    public SearchNGrams ( String dir ) {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
			envConfig.setAllowCreate(false);
			env = new Environment(new File(dir), envConfig);
		} catch ( Exception e ) { throw new RuntimeException(e); };
    }

    private SearchNGrams (Environment env) throws Exception {
        this();
    	this.env = env;
        open();
    }

    public void doWork() throws Exception {
		switch ( code ) {
			default : int n = data.split(" +").length-1;
					  data = map[n > 5 ? 5 : n].get(data).toString();
		}
    }

    private void open() throws Exception {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(false);
        TupleBinding<String> keyBinding = TupleBinding.getPrimitiveBinding(String.class);
        TupleBinding<Long> dataBinding = TupleBinding.getPrimitiveBinding(Long.class);
        for (int i=0; i<6; i++) {
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

	private static final String CONFIG_FILE = "ngramindex.properties";
	private static final String DEFAULT_CONFIG_FILE = "default.properties";
	
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
			loadPropertyFromStream(props,SearchNGrams.class.getResourceAsStream(DEFAULT_CONFIG_FILE));
			loadPropertyFromStream(props,Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));
			File f = new File(CONFIG_FILE);
			if (f.exists()) loadPropertyFromStream(props, new FileInputStream(f));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		return props;
	}
	
	public String getNGramFrequency ( String data[] ) {
		return getNGramFrequency(data, null);
	}
	
	public String getNGramFrequency ( String s[], String output ) {
		if(s.length==0) return "0";
		String data = s[0];
		for (int i=1; i<s.length; i++) data += " " + s[i];
		return getNGramFrequency( data, output );
	}
	
	public String getNGramFrequency ( String data ) {
		return getNGramFrequency(data, null);
	}

	public String getNGramFrequency ( String data, String output ) {
		try {
			SearchNGrams worker = new SearchNGrams(env);
			TransactionRunner runner = new TransactionRunner(env);
			worker.data = data;
			try { runner.run(worker); } finally { worker.close(); }
			if("json".equals(output)) return "{\"query\":\""+ data + "\",\"frequency\":" + worker.data + "}";
			if("xml".equals(output)) return "<ngram-frequency><query><[!CDATA["+ data + "]]></query><frequency>" + worker.data + "</frequency></ngram-frequency>";
			else return worker.data;
		} catch ( Exception e ) { throw new RuntimeException(e); }
	}

}

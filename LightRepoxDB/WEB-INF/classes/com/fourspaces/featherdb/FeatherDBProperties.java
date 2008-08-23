package com.fourspaces.featherdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import net.weta.dfs.config.Configuration;

import com.fourspaces.featherdb.utils.Logger;

public class FeatherDBProperties {
	
	private static final String CONFIG_FILE = "featherdb.properties";
	private static final String DEFAULT_CONFIG_FILE = "default.properties";
	private static final Logger log = Logger.get(FeatherDBProperties.class);
	
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
	
	public static Properties getProperties() {
		Properties props = new Properties();
		try {
			// load the defaults from the classpath (package level file)
			loadPropertyFromStream(props,FeatherDBProperties.class.getResourceAsStream(DEFAULT_CONFIG_FILE));

			// load the config from the classpath
			loadPropertyFromStream(props,Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));

			// load additional config from the current working directory
			File f = new File(CONFIG_FILE);
			if (f.exists()) {
				loadPropertyFromStream(props, new FileInputStream(f));
			}
			ByteArrayOutputStream sw = new ByteArrayOutputStream();
			props.store(sw, "Running configuration");
			sw.close();
			log.info(new String(sw.toByteArray()));
			String mode = props.getProperty("backend.class");
			String views = props.getProperty("view.class");
			if ( (mode!=null && mode.equals("com.fourspaces.featherdb.backend.WetaDFSBackend")) ||
				 (views!=null && views.equals("com.fourspaces.featherdb.views.WetaDFSViewManager"))) {
				props.setProperty("start.weta.dfa","true");
				new File(props.getProperty("chunkDirectory")).mkdirs();
			}
		} catch (IOException e) {
			log.error("Error loading properties file",e);
			throw new RuntimeException(e);
		}		
		return props;
	}
}

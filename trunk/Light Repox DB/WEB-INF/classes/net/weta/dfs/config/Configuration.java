/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/config/Configuration.java,v $
 */

package net.weta.dfs.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.PropertyConfigurator;

/**
 * A Configuration that hold parsed configurrations in a singleton.
 * 
 * <br/><br/>created on 13.02.2004
 * 
 * @version $Revision: 1.13 $
 */
public class Configuration {

    private static Configuration fInstance = null;

    private Properties fProperties = null;

    /**
     * Comment for <code>DFS_HOME_NAME</code>
     */
    public static final String DFS_HOME_DIR = "DFS_HOME_DIR";

    /**
     * Comment for <code>DFS_CONFIG_PATH</code>
     */
    public static final String DFS_CONFIG_PATH = "DFS_CONFIG_PATH";

    /**
     * Comment for <code>PROPERTY_FILE_NAME</code>
     */
    public static final String DFS_PROPERTY_FILE_NAME = "weta-dfs.properties";

    /**
     * Comment for <code>LOG4J_FILE_NAME</code>
     */
    public static final String LOG4J_PROPERTY_FILE_NAME = "log4j.properties";

    /**
     * Comment for <code>DATA_NODE_PORT</code>
     */
    public static final String DATA_NODE_PORT = "dataNodePort";

    /**
     * Comment for <code>META_DATA_SERVER_PORT</code>
     */
    public static final String META_DATA_SERVER_PORT = "metaDataServerPort";

    /**
     * Comment for <code>CHUNK_DIRECTORY</code>
     */
    public static final String CHUNK_DIRECTORY = "chunkDirectory";

    /**
     * Comment for <code>CHUNK_DISK_SIZE</code>
     */
    public static final String CHUNK_DISK_SIZE = "chunkDiskSize";

    /**
     * Comment for <code>META_DATA_SERVER_IP</code>
     */
    public static final String META_DATA_SERVER_IP = "metaDataServerIp";

    /**
     * A private constructor since it is a singleton.
     * 
     * @throws IOException
     */
    private Configuration() throws IOException {
        this.fProperties = new Properties();
        readPropertiesFromFile();
    }

    /**
     * This object is a singleton so only this method return an instance of the
     * Setup object
     * 
     * @return Setup
     * @throws IOException
     */
    public static Configuration getInstance() throws IOException {
        if (fInstance == null)
            fInstance = new Configuration();
        return fInstance;
    }

    /**
     * @param key
     * @return Returns a value to a key setuped in the classify.properties
     */
    public String getProperty(String key) {
        return this.fProperties.getProperty(key);
    }

    /**
     * Returns propties seperated by comma
     * 
     * @param key
     * @return string array of properties
     */
    public String[] getProperties(String key) {
        String property = this.fProperties.getProperty(key);
        StringTokenizer tokenizer = new StringTokenizer(property, ",");
        ArrayList arrayList = new ArrayList();
        while (tokenizer.hasMoreElements()) {
            String element = (String) tokenizer.nextElement();
            arrayList.add(element);
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    /**
     * @param key
     * @return the property as int, if it is a int
     */
    public int getPropertyAsInt(String key) {
        return Integer.parseInt(this.fProperties.getProperty(key));
    }

    /**
     * @throws IOException
     */
    public void reset() throws IOException {
        synchronized (fInstance) {
            this.fProperties.clear();
            readPropertiesFromFile();
        }
    }

    /**
     * @throws IOException
     * 
     */
    private void readPropertiesFromFile() throws IOException {

        String dfsConf = System.getProperty(DFS_CONFIG_PATH);
        String configPath = null;

        InputStream inputStream;
        if (dfsConf != null && dfsConf.trim() != "") {
            // read from DFS_CONFIG_PATH
            configPath = dfsConf;
            inputStream = new FileInputStream(new File(configPath, DFS_PROPERTY_FILE_NAME));
            // TODO : read log4j.props
            // PropertyConfigurator.configure(new File(configPath, LOG4J_PROPERTY_FILE_NAME).getPath());
        } else {
            // read from classpath
            configPath = "/" + DFS_PROPERTY_FILE_NAME;
            inputStream = Configuration.class.getResourceAsStream(configPath);
        }

        if (inputStream == null || inputStream.available() <= 0)
            throw new IOException("could not read property file from "
                    + configPath);

        this.fProperties.load(inputStream);
        inputStream.close();

        // put PATHES to properties
        this.fProperties.put(DFS_CONFIG_PATH, configPath);
        String homeDir = System.getProperty(DFS_HOME_DIR);
        if (homeDir != null)
            this.fProperties.put(DFS_HOME_DIR, homeDir);
    }
}

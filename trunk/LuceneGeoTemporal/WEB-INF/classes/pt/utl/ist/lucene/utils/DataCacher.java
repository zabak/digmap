package pt.utl.ist.lucene.utils;

import pt.utl.ist.lucene.config.ConfigProperties;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.io.*;

import org.apache.log4j.Logger;

/** A simple singleton HashMap. */

public class DataCacher implements IDataCacher {
    private HashMap caches;

    private static final String globalTableName = "global_vars";

    public DataCacher() {
        caches = new HashMap();
    }



    private Object[] getTable(Object key) {
        return (Object[]) caches.get(key);
    }

    public void createCache(Object key, int size)
            throws IllegalArgumentException {
        if (getTable(key) == null) {
            Object[] table = new Object[size];
            caches.put(key, table);
        } else {
            throw new IllegalArgumentException(key + " already exists");
        }
    }

    public void clear() {
        caches.clear();
    }

    public Object get(Object table, int key) {
        Object[] arr = getTable(table);
        if (key < 0 || arr == null || key > arr.length - 1) {
            return null;
        }
        return arr[key];
    }

    public Object put(Object table, int key, Object value) {
        Object[] arr = getTable(table);
        if (key < 0 || arr == null || key > arr.length - 1) {
            return null;
        }
        arr[key] = value;
        return caches.put(table, arr);
    }

    public Object get(Object key) {
        return caches.get(key);
    }

    public Object put(Object key, Object value) {
        return caches.put(key, value);
    }

    public void loadFromFiles(String directory, boolean permitIdsCacheIfConfigured)
    {
        File file = new File(directory);
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].indexOf(".cache") != -1)
                        {
                            if(ConfigProperties.getBooleanProperty("cache.use." + files[i]))
                                loadFromFile(directory + File.separator + files[i],permitIdsCacheIfConfigured);
                        }
                    }
                }
            }
        }
    }

    private static final Logger logger = Logger.getLogger(DataCacher.class);
    public void loadFromFile(String fileName, boolean permitIdsCacheIfConfigured) {
        logger.info("loading cache: " + fileName);
        BufferedReader input = null;
        String line = "";
        String table = fileName;
        File file = new File(fileName);
//        table = table.substring(table.lastIndexOf("/") + 1);
        table = file.getName().substring(0, file.getName().indexOf("."));
        try {
            input = new BufferedReader(new FileReader(fileName));

            int fileLength = getNumLines(fileName) + 1;
            if(caches.get(table) != null)
            {
                logger.info("Cache: " + table + ".cache already exist in other index, will ignore this one - Opening cache " + fileName);
                return;
            }
            if(table.equals("docid"))
            {
                if(!permitIdsCacheIfConfigured)
                {
                    logger.info("Cache: " + table + ".cache have no permission to startup at this cacher " + fileName);
                    return;
                }
                else
                    logger.info("Cache: " + table + ".cache will be loaded at this cacher " + fileName);
            }
            createCache(table, fileLength);

            int k;
            String key;
            String value;
            String[] lineParts;

            while ((line = input.readLine()) != null) {
                Integer valueInt = null;
                Integer keyInt = null;
                Float valueFloat = null;
                if (!line.startsWith("#")) {
                    lineParts = line.split("[\t ]");
                    try {
                        key = lineParts[0];
                        value = lineParts[1];
                        if (table.equals(globalTableName)) {
                            valueInt = null;
                            try {
                                valueInt = Integer.valueOf(value);
                            } catch (Exception e) {}
                            if (valueInt != null)
                                put(key, valueInt);
                            else
                                put(key, value);
                        } else {
                            keyInt = Integer.valueOf(key);
                            k = keyInt.intValue();
                            try {
                                valueInt = Integer.valueOf(value);
                            } catch (Exception e) {}
                            if (valueInt == null) {
                                try {
                                    valueFloat = Float.valueOf(value);
                                } catch (Exception e) {}
                                if (valueFloat == null) {
                                    put(table, k, value);
                                } else {
                                    put(table, k, valueFloat);
                                }
                            } else {
                                put(table, k, valueInt);
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println(
                                "Suspicious line in " + fileName + ": " + line);
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getNumLines(String fileName) {
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        int c;
        int count = 0;
        try {
            while ((c = input.read()) != -1) {
                if (c == '\n') {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void writeToFiles(String directory) throws IOException {
        Set tables = caches.keySet();
        Iterator iter = tables.iterator();
        String globalData = "";
        // first write "standard" files
        while (iter.hasNext()) {
            String table = (String) iter.next();
            if (caches.get(table).getClass().isArray()) {
                writeToFile(directory, table);
            } else {
                globalData += table + " " + caches.get(table) + "\n";
            }
        }
        // now the global data
        String fileName = directory + "/" + globalTableName + ".cache";
        FileWriter writer = new FileWriter(fileName);
        BufferedWriter output = new BufferedWriter(writer);
        output.write(globalData);
        output.close();
    }

    private void writeToFile(String directory, String table)
            throws IOException {
        String fileName = directory + "/" + table + ".cache";
        FileWriter writer = new FileWriter(fileName);
        BufferedWriter output = new BufferedWriter(writer);
        Object objectTable[] = getTable(table);
        for (int i = 0; i < objectTable.length; ++i) {
            output.write(i + " " + objectTable[i] + "\n");
        }
        output.close();
    }
}

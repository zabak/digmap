package org.apache.lucene.ilps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//import java.util.StringTokenizer;
//import java.util.Vector;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** A simple singleton HashMap. */

public class DataCacher {
	private static DataCacher instance;
	private HashMap caches;

	private static final String globalTableName = "global_vars";

	private DataCacher() {
		caches = new HashMap();
	}

	public static synchronized DataCacher Instance() {
		if (instance == null) {
			instance = new DataCacher();
		}
		return instance;
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

	public void loadFromFiles(String directory) {
		File file = new File(directory);
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].indexOf(".cache") != -1)
							loadFromFile(directory + "/" + files[i]);
					}
				}
			}
		}
	}

	public void loadFromFile(String fileName) {
		BufferedReader input = null;
		String line = "";
		String table = fileName;
		table = table.substring(table.lastIndexOf("/") + 1);
		table = table.substring(0, table.indexOf("."));
		try {
			input = new BufferedReader(new FileReader(fileName));

			int fileLength = getNumLines(fileName) + 1;
			createCache(table, fileLength);                                    

			int k = -1;
			String key = null;
			String value = null;
			String[] lineParts = null;

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

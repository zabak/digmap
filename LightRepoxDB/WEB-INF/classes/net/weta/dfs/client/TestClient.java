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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/TestClient.java,v $
 */

package net.weta.dfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.FeatherDBProperties;

import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.util.FileUtil;

/**
 * Executable to read/write from/to files on the DFS.
 * 
 * <br/><br/>created on 19.03.2005
 * 
 * @version $Revision: 1.1 $
 *  
 */
public class TestClient {

    private static final String fParam1 = "-w";

    private static final String fParam2 = "-r";

    private static final String fParam3 = "-d";

    private static final String fParam4 = "-c";
    
    private static final String fParam5 = "-m";

    private static final long fChunkSize = 64;

    /**
     * @param file
     * @param name
     * @throws IOException
     */
    public static void writeFile(File file, String name) throws IOException {

        if (!file.exists())
            throw new IOException("file " + file.getPath() + " does not exists");
        if (!file.canRead())
            throw new IOException("file " + file.getPath() + " cannot be read");

        DFile dFile = new DFile(name, getIpAdress(), getPort());
        DFile parent= dFile.getParentFile();
        if (parent.mkdirs()) {
            throw new IOException("cannot create directory " + dFile.getParentFile().getAbsolutePath());
        }
        parent.getMDSConnection().close();

        System.out.println("writing file " + file.getPath() + " under path "
                + dFile.getPath() + " to dfs");

        DFileOutputStream outputStream = new DFileOutputStream(dFile,
                fChunkSize);
        FileInputStream fiStream = new FileInputStream(file);
        FileUtil.writeBytes(fiStream, outputStream);
        fiStream.close();
        outputStream.close();

        if (dFile.exists())
            System.out.println("file " + file.getPath()
                    + " is written succesfully to remote path "
                    + dFile.getPath());
        else
            System.out.println("writing file " + file.getPath()
                    + " wrecked for unknown reasons");

        dFile.getMDSConnection().close();
    }

    /**
     * @param file
     * @param name
     * @throws IOException
     */
    public static void readFile(File file, String name) throws IOException {
        DFile dFile = new DFile(name, getIpAdress(), getPort());
        if (!dFile.exists())
            throw new IOException("distributed file " + dFile.getPath()
                    + " does not exist");

        if (!file.createNewFile())
            throw new IOException("cannot create file " + file.getPath());

        DFileInputStream inputStream = new DFileInputStream(dFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileUtil.writeBytes(inputStream, fileOutputStream);

        fileOutputStream.close();
        inputStream.close();

        System.out.println("file is written successfully to local path "
                + file.getPath());
        dFile.getMDSConnection().close();
    }

    /**
     * @param name
     * @throws IOException
     */
    public static void deleteFile(String name) throws IOException {
        DFile dFile = new DFile(name, getIpAdress(), getPort());
        if (!dFile.exists())
            throw new IOException("distributed file " + dFile.getPath()
                    + " does not exist");
        if (dFile.delete())
            System.out.println("file " + name + " is deleted successfully");
        else
            System.out.println("deleting file " + name
                    + " wrecked for unknown reasons");
        dFile.getMDSConnection().close();
    }
    
    /**
     * @param name
     * @throws IOException
     */
    public static void createDirectory(String name) throws IOException {
    	DFile dFile = new DFile(name, getIpAdress(), getPort());
        if (dFile.exists())
            throw new IOException("distributed file/directory " + dFile.getPath()
                    + " already exists");        
        if (dFile.mkdir())
            System.out.println("directory " + name + " is created successfully");
        else
            System.out.println("creating directory " + name
                    + " wrecked for unknown reasons");        
        dFile.getMDSConnection().close();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
		if (args.length < 2) {
            printUsage();
            return;
        }
		
		MetaDataServer fMds = null;
		DataNodeServer fDns = null;
		try {
			fMds = new MetaDataServer(Configuration.getInstance());
			fMds.startServer();
		} catch ( Exception e ) { fMds = null; }
		try {
			fDns = new DataNodeServer(Configuration.getInstance());
			fDns.startServer();
		} catch ( Exception e ) { fDns = null; }
		
        if (args[0].equals(fParam1)) {
            File file = new File(args[1]);
            writeFile(file, args[2]);
        } else if (args[0].equals(fParam2)) {
            File file = new File(args[2]);
            readFile(file, args[1]);
        } else if (args[0].equals(fParam3)) {
            deleteFile(args[1]);
        } else if (args[0].equals(fParam5)) {
            createDirectory(args[1]);
        } else if (args[0].equals(fParam4)) {
            String name = "/tempFile";
            File fileSrc = new File(args[1]);
            writeFile(fileSrc, name);
            File fileDest = new File(args[2]);
            try {
                readFile(fileDest, name);
                System.out.println("copied file " + fileSrc.getPath()
                        + " successfully to " + fileDest.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                deleteFile(name);
            }
        } else {
            System.out.println(" option " + args[0] + " does not exists");
            printUsage();
        }
        try { fMds.stopServer(); } catch ( Exception e ) { }
        try { fDns.stopServer(); } catch ( Exception e ) { }
    }

    /**
     *  
     */
    public static void printUsage() {
        System.out.println("Usage : java  net.weta.dfs.utils.TestClient [-option] [args...]\n");
        System.out.println("where option could be: \n");
        System.out.println("\t -w <location> <pathName> \t for writing a local file under given pathName to the distributed file system");
        System.out.println("\t -r <pathName> <location> \t for reading a file with the given pathName from distributed file system to local location");
        System.out.println("\t -d <pathName> \t for deleting a file with the given pathName from distributed file system");
        System.out.println("\t -c <location1> <location2> \t for writing a local file specified by location1 in dfs and reading it back to location2");
        System.out.println("\t -m <pathName> \t for creating a directory in the given pathName to the distributed file system");
        System.out.println();
    }

    private static String getIpAdress() throws IOException {
        return Configuration.getInstance().getProperty(
                Configuration.META_DATA_SERVER_IP);
    }

    private static int getPort() throws IOException {
        return Configuration.getInstance().getPropertyAsInt(
                Configuration.META_DATA_SERVER_PORT);
    }
}
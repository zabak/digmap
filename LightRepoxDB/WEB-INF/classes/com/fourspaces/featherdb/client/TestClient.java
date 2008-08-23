package com.fourspaces.featherdb.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.weta.dfs.util.FileUtil;

import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.FeatherDBProperties;
import com.fourspaces.featherdb.backend.Backend;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.JSONDocument;

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
    
    private static final String fParam4 = "-m";

    public static void writeFile(File file, Backend db, String dname, String name) throws Exception {
    	if (!file.exists())
            throw new IOException("file " + file.getPath() + " does not exists");
        if (!file.canRead())
            throw new IOException("file " + file.getPath() + " cannot be read");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileInputStream fiStream = new FileInputStream(file);
        FileUtil.writeBytes(fiStream, outputStream);
        fiStream.close();
        
		JSONDocument newdoc = (JSONDocument)Document.newDocument(db, dname, name, "test-user");
		newdoc.put("data", new String(outputStream.toByteArray()));
		db.saveDocument(newdoc);
        
		db.doesDocumentExist(dname, name);
        if (db.doesDocumentExist(dname, name))
            System.out.println("file " + file.getPath()
                    + " is written succesfully with id "
                    + name);
        else
            System.out.println("writing file " + name
                    + " wrecked for unknown reasons");
    }

    public static void readFile(File file, Backend db, String dname, String name) throws IOException {
    	if (!db.doesDatabaseExist(dname))
            throw new IOException("distributed database " + dname
                    + " does not exist");        
    	if (!db.doesDocumentExist(dname, name))
            throw new IOException("distributed file " + name
                    + " does not exist");

        if (!file.createNewFile())
            throw new IOException("cannot create file " + file.getPath());

		Document myDoc = db.getDocument("foodb","documentid1234");
        
        InputStream inputStream = new ByteArrayInputStream(myDoc.toString().getBytes());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileUtil.writeBytes(inputStream, fileOutputStream);

        fileOutputStream.close();
        inputStream.close();
        System.out.println("file is written successfully to local path "
                + file.getPath());
    }

    public static void deleteFile(Backend db, String dname, String name) throws Exception {
    	if (!db.doesDatabaseExist(dname))
            throw new IOException("distributed database " + dname
                    + " does not exist");        
    	if (!db.doesDocumentExist(dname, name))
            throw new IOException("distributed file " + name
                    + " does not exist");
    	db.deleteDocument(dname,name);
    	if (!db.doesDocumentExist(dname, name))
            System.out.println("file " + name + " is deleted successfully");
        else
            System.out.println("deleting file " + name
                    + " wrecked for unknown reasons");
    }
    
    public static void createDatabase(Backend db, String dname) throws Exception {
    	if (db.doesDatabaseExist(dname))
            throw new IOException("distributed database " + dname
                    + " already exists");
    	db.addDatabase(dname);
    	
    	if (db.doesDatabaseExist(dname))
            System.out.println("database " + dname + " is created successfully");
        else
            System.out.println("creating database " + dname
                    + " wrecked for unknown reasons");        
    }

    public static void main(String[] args) throws Exception {
    	String remoteLocation = "127.0.0.1:8080/lightrepoxdb";
    	Backend db;
    	
    	if(remoteLocation==null) {
    		FeatherDB fdb = new FeatherDB();		
    		db = fdb.getBackend();
    		db.init(fdb);    	
    	} else {
    		db = new Session(remoteLocation);
    	}
		
    	if (args.length < 2) {
            printUsage();
            return;
        }		
        if (args[0].equals(fParam1)) {
            File file = new File(args[1]);
            writeFile(file, db, args[2], args[3]);
        } else if (args[0].equals(fParam2)) {
            File file = new File(args[3]);
            readFile(file, db, args[1], args[2]);
        } else if (args[0].equals(fParam3)) {
            deleteFile(db, args[1], args[2]);
        } else if (args[0].equals(fParam4)) {
            createDatabase(db, args[1]);
        } else {
            System.out.println(" option " + args[0] + " does not exists");
            printUsage();
        }
    }

    public static void printUsage() {
        System.out.println("Usage : java com.fourspaces.featherdb.test.TestClient [-option] [args...]\n");
        System.out.println("where option could be: \n");
        System.out.println("\t -w <location> <db> <pathName> \t for writing a local file under given pathName to the distributed file system");
        System.out.println("\t -r <db> <pathName> <location> \t for reading a file with the given pathName from distributed file system to local location");
        System.out.println("\t -d <db> <pathName> \t for deleting a file with the given pathName from distributed file system");
        System.out.println("\t -m <name> \t for creating a database with the given name in the distributed file system");
        System.out.println();
    }

}
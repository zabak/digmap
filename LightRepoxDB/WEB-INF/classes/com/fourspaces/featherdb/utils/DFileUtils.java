package com.fourspaces.featherdb.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import com.colloquial.arithcode.ArithCodeInputStream;
import com.colloquial.arithcode.AdaptiveUnigramModel;
import com.colloquial.arithcode.ArithCodeModel;
import com.colloquial.arithcode.ArithCodeOutputStream;
import com.colloquial.arithcode.PPMModel;
import com.colloquial.arithcode.UniformModel;
import com.fourspaces.featherdb.FeatherDBProperties;

import net.weta.dfs.client.DFile;
import net.weta.dfs.client.DFileInputStream;
import net.weta.dfs.client.DFileOutputStream;

public class DFileUtils {
	
	public static int chunkSize = 64;
	
	private static ArithCodeModel compressionModel = null;

    private static DFileUtils fInstance = getInstance();
	
    private DFileUtils() {
    	String model = FeatherDBProperties.getProperties().getProperty("backend.fs.compressionmodel");
    	if(model==null) compressionModel = null;
    	else if(model.equals("AdaptiveUnigramModel")) compressionModel = new AdaptiveUnigramModel();
    	else if(model.equals("UniformModel")) compressionModel = UniformModel.MODEL;
    	else if(model.equals("PPMModel")) compressionModel = new PPMModel(16);
    	else throw new RuntimeException("Compression Model: " + model + " not valid!");
    }
    
    public static DFileUtils getInstance() {
        if (fInstance == null) fInstance = new DFileUtils();
        return fInstance;
    }
		
	public static void writeToFile(DFile file, String str) throws IOException {
		writeToFile(file,str,false);
	}
	
	public static void writeToFile(DFile file, String str, boolean append) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(getDFileWriter(file,append));
			writer.write(str);
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (Exception e) {
				}	
			}
		}
	}
	
	public static String readFileAsString(DFile file) throws IOException {
		final StringBuilder buffer = new StringBuilder("");
		readFileByLine(file, new LineCallback() {
			public void process(String line) {
				buffer.append(line);
				buffer.append("\n");
			}});
		return buffer.toString();
	}
	
	public static void readFileByLine(DFile file, LineCallback callback) throws IOException {
		String line = null;
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(getDFileReader(file));
			while ((line=reader.readLine())!=null) {
				callback.process(line);
			}
		} finally {
			if (reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}
		
	public static InputStream getDFileInputStream(DFile f) throws IOException {
		if (compressionModel!=null) return new ArithCodeInputStream(new DFileInputStream(f),compressionModel);
		else return new DFileInputStream(f);
	}
	
	public static OutputStream getDFileOutputStream(DFile f) throws IOException {
		if (compressionModel!=null) return new ArithCodeOutputStream(new DFileOutputStream(f,chunkSize),compressionModel);
		else return new DFileOutputStream(f,chunkSize);
	}
	
	public static Reader getDFileReader(DFile f) throws IOException {
		if (compressionModel!=null) return new java.io.InputStreamReader(new ArithCodeInputStream(new DFileInputStream(f),compressionModel));
		else return new java.io.InputStreamReader(new DFileInputStream(f));
	}
	
	public static java.io.Writer getDFileWriter(DFile f) throws IOException {
		if (compressionModel!=null) return new java.io.OutputStreamWriter(new ArithCodeOutputStream(new DFileOutputStream(f,chunkSize),compressionModel));
		else return new java.io.OutputStreamWriter(new DFileOutputStream(f,chunkSize));
	}
	
	// TODO : check is this is working
	public static java.io.Writer getDFileWriter(DFile f, boolean append) throws IOException {
		if(append && f.exists()) {
			return getDFileWriter(f);		
		} else {
			if(f.exists()) f.delete();
			f.createNewFile();
			return getDFileWriter(f);
		}
	}
	
}

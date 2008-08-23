package com.fourspaces.featherdb.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import com.colloquial.arithcode.AdaptiveUnigramModel;
import com.colloquial.arithcode.ArithCodeInputStream;
import com.colloquial.arithcode.ArithCodeModel;
import com.colloquial.arithcode.ArithCodeOutputStream;
import com.colloquial.arithcode.PPMModel;
import com.colloquial.arithcode.UniformModel;
import com.fourspaces.featherdb.FeatherDBProperties;

public class FileUtils {
	
	private static ArithCodeModel compressionModel = null;

    private static FileUtils fInstance = getInstance();
	
    private FileUtils() {
    	String model = FeatherDBProperties.getProperties().getProperty("backend.fs.compressionmodel");
    	if(model==null || model.equals("null")) compressionModel = null;
    	else if(model.equals("AdaptiveUnigramModel")) compressionModel = new AdaptiveUnigramModel();
    	else if(model.equals("UniformModel")) compressionModel = UniformModel.MODEL;
    	else if(model.equals("PPMModel")) compressionModel = new PPMModel(16);
    	else throw new RuntimeException("Compression Model: " + model + " not valid!");
    }
    
    public static FileUtils getInstance() {
        if (fInstance == null) fInstance = new FileUtils();
        return fInstance;
    }
    
	public static void writeToFile(File file, String str) throws IOException {
		writeToFile(file,str,false);
	}
	
	public static void writeToFile(File file, String str, boolean append) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(getFileWriter(file,append));
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
	
	public static String readFileAsString(File file) throws IOException {
		final StringBuilder buffer = new StringBuilder("");
		readFileByLine(file, new LineCallback() {
			public void process(String line) {
				buffer.append(line);
				buffer.append("\n");
			}});
		return buffer.toString();
	}
	
	public static void readFileByLine(File file, LineCallback callback) throws IOException {
		String line = null;
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(getFileReader(file));
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
		
	public static InputStream getFileInputStream(File f) throws IOException {
		if (compressionModel!=null) return new ArithCodeInputStream(new FileInputStream(f),compressionModel);
			else 
		return new FileInputStream(f);
	}
	
	public static OutputStream getFileOutputStream(File f) throws IOException {
		if (compressionModel!=null) return new ArithCodeOutputStream(new FileOutputStream(f),compressionModel);
		else return new FileOutputStream(f);
	}
	
	public static Reader getFileReader(File f) throws IOException {
		if (compressionModel!=null) return new java.io.InputStreamReader(new ArithCodeInputStream(new FileInputStream(f),compressionModel));
		else return new FileReader(f);
	}
	
	public static java.io.Writer getFileWriter(File f) throws IOException {
		if (compressionModel!=null) return new java.io.OutputStreamWriter(new ArithCodeOutputStream(new FileOutputStream(f),compressionModel));
		else return new FileWriter(f);
	}
	
	public static java.io.Writer getFileWriter(File f, boolean append) throws IOException {
		if (compressionModel!=null) return new java.io.OutputStreamWriter(new ArithCodeOutputStream(new FileOutputStream(f,append),compressionModel));
		else return new FileWriter(f,append);
	}
	
}
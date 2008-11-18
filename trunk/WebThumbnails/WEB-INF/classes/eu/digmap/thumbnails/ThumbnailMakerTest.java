package eu.digmap.thumbnails;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

public class ThumbnailMakerTest {
	
	public static void main(String[] args) throws Exception {
		final List<Exception> exceptions = new ArrayList<Exception>();
		final File targetImageFile = new File(ThumbnailConfig.INSTANCE.webappRoot, "Working.png");
		final String targetImageUrl = targetImageFile.toURL().toString();
		
		Runnable client = new Runnable() {
			public void run() {
				InputStream inputStream = null;
				try {
					int width = 100 + (int)(Math.random() * 412);
					int height = 100 + (int)(Math.random() * 412);
					ThumbnailParams params = new ThumbnailParams(
							targetImageUrl,
							null,
							width,
							height,
							0
						);
					AbstractThumbnailMaker thumbnailMaker = ThumbnailMakerFactory.getThumbnailMaker(params);
					
					File outputFile = File.createTempFile("thumb", ".png");
					outputFile.deleteOnExit();
					OutputStream outputStream = new BufferedOutputStream( new FileOutputStream(outputFile) );
					thumbnailMaker.make(true, outputStream);
					outputStream.flush();
					outputStream.close();
					outputFile.delete();
				} catch (Exception e) {
					exceptions.add(e);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException e) {
						exceptions.add(e);
					}
				}
			}
		};
		
		int clients = 500;
		List<Thread> threads = new ArrayList<Thread>();
		long t1 = new Date().getTime();

		printMem("mem time1");
		for (int i=0; i<clients; i++) {
			Thread thread = new Thread(client);
			threads.add(thread);
		}
		printMem("mem time2");

		long t2 = new Date().getTime();
		for (Thread thread : threads) {
			thread.start();
		}

		for (int i=0; i<threads.size(); i++) {
			Thread thread = threads.get(i);
			thread.join();
		}
		long t3 = new Date().getTime();

		for (int i = 0; i < exceptions.size(); i++) {
			Exception e = exceptions.get(i);
			System.out.printf("exception(%s): %s%n", i, e.getMessage() );
			if (true) {
				e.printStackTrace();
			}
		}

		printTime("time1-2", t1, t2);
		printTime("time2-3", t2, t3);
		printTime("time1-3", t1, t3);
		printMem("mem time3");
		System.gc();
		printMem("mem time3 gc");
	}
	
	private static void printTime(String desc, long t1, long t2) {
		System.out.printf("%s: %.2f%n", desc, (t2-t1)/1000.0 );
	}
	
	private static void printMem(String desc) {
		Runtime rt = Runtime.getRuntime();
		float mbUnit = 1.0f * 1024 * 1024;
		System.out.printf(
				"%s - totalMemory: %.2fMB | FreeMemory: %.2fMB | MaxMemory: %.2fMB%n",
				desc,
				rt.totalMemory() / mbUnit,
				rt.freeMemory() / mbUnit,
				rt.maxMemory() / mbUnit);
	}
}

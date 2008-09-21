package eu.digmap.thumbnails;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.*;

import javax.imageio.ImageIO;

/** Producer-Consumer in Java, for J2SE 1.5 using concurrent. */
public class AsynchronosGenerator {

  protected boolean done = false;
  
  protected BlockingQueue<AbstractThumbnailMaker> myQueue;
  
  protected static AsynchronosGenerator theInstance = new AsynchronosGenerator();

  /** Inner class representing the Consumer side */
  class Consumer implements Runnable {
    protected BlockingQueue<AbstractThumbnailMaker> queue;
    Consumer(BlockingQueue<AbstractThumbnailMaker> theQueue) { this.queue = theQueue; }
    public void run() {
      try {
        while (true) {
          AbstractThumbnailMaker obj = queue.take();
          process(obj);
          if (done) { return; }
        }
      } catch (InterruptedException ex) {
          System.out.println("THUMBNAILS GENERATOR WAS INTERRUPTED");
      }
    }

    void process(AbstractThumbnailMaker obj) {
		try {
			System.out.println("Generating thumbnail for " + obj.uri);
			if(obj.rotation==Float.MAX_VALUE) obj.rotation = 0;
			String file = "0000" + (obj.uri + obj.width + obj.height + obj.rotation).hashCode();
			String dir1 = file.substring(file.length()-2);
			String dir2 = file.substring(file.length()-4,file.length()-2);
			file = obj.cacheDirectory+File.separator+dir1+File.separator+dir2+File.separator+file.substring(0,file.length()-4);
			(new File(obj.cacheDirectory+File.separator+dir1)).mkdir();
			(new File(obj.cacheDirectory+File.separator+dir1+File.separator+dir2)).mkdir();
			if(obj.cacheDuration<=0) (new DataOutputStream(new FileOutputStream(file+".duration"))).writeLong(-1);
			else (new DataOutputStream(new FileOutputStream(file+".duration"))).writeLong(System.currentTimeMillis()+obj.cacheDuration);
			ImageIO.write(obj.getImage(),"png",new FileOutputStream(file+".png"));
		} catch ( Exception e ) { 
			
		}
    }
  }
  
  private AsynchronosGenerator() {
	  this(5);
  }

  private AsynchronosGenerator(int nC) {
	myQueue = new LinkedBlockingQueue<AbstractThumbnailMaker>();
    for (int i=0; i<nC; i++) new Thread(new Consumer(myQueue)).start();
  }
  
  public static AsynchronosGenerator getInstance() { return theInstance; }
  
  public void addRequest ( AbstractThumbnailMaker obj ) {
	  myQueue.add(obj);
  }

}

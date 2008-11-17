package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Producer-Consumer in Java, for J2SE 1.5 using concurrent. */
public class AsynchronousGenerator {

  protected boolean done = false;
  
  protected BlockingQueue<AbstractThumbnailMaker> myQueue;
  private Cache cache = Cache.INSTANCE; 
  
  protected static AsynchronousGenerator theInstance = new AsynchronousGenerator();

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
			System.out.println("Generating thumbnail for " + obj.params.uri);
			BufferedImage fullImage = cache.getFromCache(obj.params.uri, 0, 0);
			if (fullImage == null) {
				fullImage = obj.getImage();
				cache.putInCache(obj.params.uri, 0, 0, fullImage, false);
			} else {
				System.err.println("using full image from cache in async");
			}
			cache.putInCache(obj.params.uri, obj.params.width, obj.params.height, obj.scaleImage(fullImage), false);
			
			System.out.println( "" + fullImage != null );
		} catch ( Exception e ) { 
			e.printStackTrace();
		}
    }
  }
  
  private AsynchronousGenerator() {
	  this(5);
  }

  private AsynchronousGenerator(int nC) {
	myQueue = new LinkedBlockingQueue<AbstractThumbnailMaker>();
    for (int i=0; i<nC; i++) new Thread(new Consumer(myQueue)).start();
  }
  
  public static AsynchronousGenerator getInstance() { return theInstance; }
  
  public void addRequest ( AbstractThumbnailMaker obj ) {
	  myQueue.add(obj);
  }
}

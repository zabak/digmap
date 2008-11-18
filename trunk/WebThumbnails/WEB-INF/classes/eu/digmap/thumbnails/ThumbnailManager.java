package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThumbnailManager {
	protected Log log = LogFactory.getLog(getClass());
	
	public static ThumbnailManager INSTANCE = initInstance();

	private Cache cache = Cache.INSTANCE; 
	private ExecutorService syncTaskExecutor;
	private ExecutorService asyncTaskExecutor;
	protected Map<ThumbnailParams, Future<ThumbResult>> syncTasks = new HashMap<ThumbnailParams, Future<ThumbResult>>();
	protected Map<ThumbnailParams, Future<ThumbResult>> asyncTasks = new HashMap<ThumbnailParams, Future<ThumbResult>>();

	public ThumbnailManager() {
		this(10, 10);
	}
	
	private static ThumbnailManager initInstance() {
		System.err.printf("ThumbnailManager => Threads - sync:%s | async:%s%n", ThumbnailConfig.INSTANCE.syncTaskThreads, ThumbnailConfig.INSTANCE.asyncTaskThreads);
		ThumbnailManager instance = new ThumbnailManager(
				ThumbnailConfig.INSTANCE.syncTaskThreads,
				ThumbnailConfig.INSTANCE.asyncTaskThreads
			);
		return instance;
	}

	public ThumbnailManager(int syncThreads, int asyncThreads) {
		super();
		this.syncTaskExecutor = Executors.newFixedThreadPool(syncThreads);
		this.asyncTaskExecutor = Executors.newFixedThreadPool(asyncThreads);
	}
	
	public ThumbnailManager(ExecutorService syncTaskExecutor, ExecutorService asyncTaskExecutor) {
		super();
		this.syncTaskExecutor = syncTaskExecutor;
		this.asyncTaskExecutor = asyncTaskExecutor;
	}
	

	public synchronized Future<ThumbResult> execute(AbstractThumbnailMaker thumbnailMaker, boolean sync, boolean useCache) {
		Future<ThumbResult> resultHolder;

		ThumbnailParams params = thumbnailMaker.getParams();
		if (sync) {
			if (asyncTasks.containsKey(params)) {
				Future<ThumbResult> task = asyncTasks.remove(params);
				task.cancel(true);
			}
			if (syncTasks.containsKey(params)) {
				resultHolder = syncTasks.get(params);
			}
			else {
				resultHolder = syncTaskExecutor.submit(
						new ThumbnailWorker(thumbnailMaker, sync, useCache, this)
					);
				syncTasks.put(params, resultHolder);
			}
		}
		else {
			if (syncTasks.containsKey(params)) {
				resultHolder = syncTasks.get(params);
			}
			else if (asyncTasks.containsKey(params)) {
				resultHolder = asyncTasks.get(params);
			}
			else {
				resultHolder = asyncTaskExecutor.submit(
						new ThumbnailWorker(thumbnailMaker, sync, useCache, this)
					);
				asyncTasks.put(params, resultHolder);
			}
		}
		
		return resultHolder;
	}
	
	private class ThumbnailWorker implements Callable<ThumbResult> {
		protected AbstractThumbnailMaker thumbnailMaker;
		protected boolean sync = false;
		protected boolean useCache = true;
		private final ThumbnailManager thumbnailManager;
		
		public ThumbnailWorker(AbstractThumbnailMaker thumbnailMaker, boolean sync, boolean useCache, ThumbnailManager thumbnailManager) {
			super();
			this.thumbnailMaker = thumbnailMaker;
			this.sync = sync;
			this.useCache = useCache;
			this.thumbnailManager = thumbnailManager;
		}

		public ThumbResult call() {
			ThumbnailParams params = thumbnailMaker.params;
			BufferedImage fullImage;
			try {
//				System.out.println(
//						String.format("** ThumbnailWorker ** - Generating thumbnail (sync: %s, cache: %s) for '%s'",
//								sync,
//								useCache,
//								params.uri
//							)
//					);
				fullImage = useCache ? cache.getFromCache(params.uri, 0, 0) : null;
				if (fullImage == null) {
					fullImage = thumbnailMaker.getImage();
					cache.putInCache(params.uri, 0, 0, fullImage, false);
				} else {
//					System.err.println("** ThumbnailWorker ** - Using full image from cache");
				}
				cache.putInCache(params.uri, params.width, params.height, thumbnailMaker.scaleImage(fullImage), false);
				File imageFile = cache.getImageFile(params.uri, params.width, params.height);
				
				// remove from task lists
				thumbnailManager.getSyncTasks().remove(params);
				thumbnailManager.getAsyncTasks().remove(params);
				
				// use image in sync mode?
				return new ThumbResult(null, imageFile);
			} 
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public ExecutorService getSyncTaskExecutor() {
		return syncTaskExecutor;
	}

	public void setSyncTaskExecutor(ExecutorService syncTaskExecutor) {
		this.syncTaskExecutor = syncTaskExecutor;
	}

	public ExecutorService getAsyncTaskExecutor() {
		return asyncTaskExecutor;
	}

	public void setAsyncTaskExecutor(ExecutorService asyncTaskExecutor) {
		this.asyncTaskExecutor = asyncTaskExecutor;
	}

	public Map<ThumbnailParams, Future<ThumbResult>> getSyncTasks() {
		return syncTasks;
	}

	public void setSyncTasks(Map<ThumbnailParams, Future<ThumbResult>> syncTasks) {
		this.syncTasks = syncTasks;
	}

	public Map<ThumbnailParams, Future<ThumbResult>> getAsyncTasks() {
		return asyncTasks;
	}

	public void setAsyncTasks(Map<ThumbnailParams, Future<ThumbResult>> asyncTasks) {
		this.asyncTasks = asyncTasks;
	}
		
}

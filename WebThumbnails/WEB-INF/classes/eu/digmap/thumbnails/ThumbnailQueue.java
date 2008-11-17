package eu.digmap.thumbnails;

import java.util.List;

public interface ThumbnailQueue {
	void add(List<String> urls);
	boolean remove(String url);
	boolean exists(String url);
	void getWorker(String url);
}

package net.weta.dfs.client;

/**
 *
 */
public interface DFileFilter {

    /**
     * @param pathname
     * @return true or false
     */
    public boolean accept(DFile pathname);
}
 

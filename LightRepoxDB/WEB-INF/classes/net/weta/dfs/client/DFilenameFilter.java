package net.weta.dfs.client;


/**
 *
 */
public interface DFilenameFilter {

    /**
     * @param dir
     * @param name
     * @return true or false
     */
    public boolean accept(DFile dir, String name);
}

/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/ISignalsMDS.java,v $
 */

package net.weta.dfs.com;

/**
 * ISignalsMDS holds the different request signals that can be processed by
 * MetaDataServer. Use them as types in CommandRequest objects.
 * 
 * To know which value each request type needs refer to ServerFaceMDS and
 * ClientHandlerMDS.
 * 
 * <br/><br/>created on 09.05.2005
 * 
 * @version $Revision: 1.2 $
 * @see net.weta.dfs.com.CommandRequest
 * @see net.weta.dfs.server.mds.ClientHandlerMDS
 */
public interface ISignalsMDS {

    // -------------file-related--------------------------
    /**
     * Request a file to create.
     */
    public static final int FILE_CREATE = 1;

    /**
     * Request a file to delete.
     */
    public static final int FILE_DELETE = 2;

    /**
     * Request the id of a file.
     */
    public static final int FILE_GET_ID = 3;

    /**
     * Request the length of a file.
     */
    public static final int FILE_GET_LENGTH = 4;

    /**
     * Request the chunks of a file.
     */
    public static final int FILE_GET_CHUNKS = 5;

    /**
     * Request the existence of a file.
     */
    public static final int FILE_EXISTS = 6;

    /**
     * Request if a file is existent and a file(not a directory).
     */
    public static final int FILE_IS_FILE = 7;

    /**
     * Request if a file is existent and a directory.
     */
    public static final int FILE_IS_DIRECTORY = 8;

    /**
     * Request the creation of a directory with all parent directories.
     */
    public static final int FILE_MK_DIRS = 9;

    /**
     * Request an array of all filenames contained by a directory.
     */
    public static final int FILE_LIST = 10;

    /**
     * Request to set a files chunk size.
     */
    public static final int FILE_SET_MAX_CHUNK_SIZE = 11;

    // -------------node-related--------------------------

    /**
     * Request a free node.
     */
    public static final int NODE_GET_A_FREE = 21;

    /**
     * Request all nodes a chunk is located.
     */
    public static final int NODE_GET_FROM_CHUNK = 22;

    /**
     * Request initialisation of files.
     */
    public static final int NODE_INIT_FILES = 23;

    /**
     * Request initialisation of chunks.
     */
    public static final int NODE_INIT_CHUNKS = 24;

    // ------------chunk-related--------------------------

    /**
     * Notify that a chunk is written.
     */
    public static final int CHUNK_WRITTEN = 41;

    // -------------different-----------------------------

    /**
     * Request an abitrary id.
     */
    public static final int GET_AN_ID = 61;

    /**
     * Notify that node is alive and breathing.
     */
    public static final int LIFE_SIGN = 62;

    /**
     * Request the (remote)id of the caller.
     */
    public static final int GET_IP = 63;
}

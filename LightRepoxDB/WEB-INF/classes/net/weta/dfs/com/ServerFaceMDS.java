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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/ServerFaceMDS.java,v $
 */

package net.weta.dfs.com;

import java.io.IOException;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;

/**
 * You could use the MDServerFace for communication with MetaDataServer. All you
 * need is an Connection which is focused on the MetaDataServer. <br/>
 * 
 * To know which value each request type needs refer to ClientHandlerMDS.
 * 
 * <br/><br/>created on 01.05.2005
 * 
 * @version $Revision: 1.2 $
 * @see net.weta.dfs.server.mds.ClientHandlerMDS
 * @see net.weta.dfs.com.Connection
 */
public class ServerFaceMDS extends AbstractServerFace {

    // -------------file-related--------------------------
    /**
     * @param mdsConn
     * @param filePath
     * @return false if file already exists
     * 
     * @throws IOException
     * @throws CommandException
     */
    public static boolean fileCreate(Connection mdsConn, String filePath)
            throws IOException, CommandException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_CREATE,
                filePath));
        boolean notExistent = mdsConn.receiveResponse().getValueAsBoolean();

        return notExistent;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return the fileId of the file with the given pathname or null if file
     *         not exists
     * @throws IOException
     */
    public static String fileGetId(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_GET_ID,
                filePath));
        String fileId = (String) receiveResponseSilently(mdsConn).getValue();

        return fileId;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return the length of the file with the given filePath
     * @throws IOException
     */
    public static long fileGetLength(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_GET_LENGTH,
                filePath));
        long length = receiveResponseSilently(mdsConn).getValueAsLong();

        return length;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return the chunks of the file or a array of length == 0 if file has none
     * @throws IOException
     */
    public static ChunkMetaData[] fileGetChunks(Connection mdsConn,
            String filePath) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_GET_CHUNKS,
                filePath));
        ChunkMetaData[] chunkMDs = (ChunkMetaData[]) receiveResponseSilently(
                mdsConn).getValue();

        return chunkMDs;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return true if file exits, false otherwise
     * @throws IOException
     */
    public static boolean fileExists(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_EXISTS,
                filePath));
        boolean exists = receiveResponseSilently(mdsConn).getValueAsBoolean();

        return exists;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return true if file is a file, false if not exists or file is a
     *         directory
     * @throws IOException
     */
    public static boolean fileIsFile(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_IS_FILE,
                filePath));
        boolean isFile = receiveResponseSilently(mdsConn).getValueAsBoolean();

        return isFile;
    }

    /**
     * 
     * @param mdsConn
     * @param filePath
     * @return false if the file does not exsit or file is directory and not
     *         empty, true otherwise
     * @throws IOException
     */
    public static boolean fileDelete(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_DELETE,
                filePath));
        boolean isFile = receiveResponseSilently(mdsConn).getValueAsBoolean();

        return isFile;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return true if directory and all necessary parent directories was
     *         created
     * @throws IOException
     */
    public static boolean fileMkDirs(Connection mdsConn, String filePath) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_MK_DIRS, filePath));
        boolean success = receiveResponseSilently(mdsConn).getValueAsBoolean();
        return success;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return true if filePath denotes a directory, false if filePath not
     *         exists or denotes a file
     * @throws IOException
     */
    public static boolean fileIsDirectory(Connection mdsConn, String filePath) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_IS_DIRECTORY,
                filePath));
        boolean isDirectory = receiveResponseSilently(mdsConn)
                .getValueAsBoolean();

        return isDirectory;
    }

    /**
     * @param mdsConn
     * @param filePath
     * @return an array of file and directory names contained by the directory
     *         denoted by the given filePath or null if filePath denotes not an
     *         directory
     * @throws IOException
     */
    public static String[] fileList(Connection mdsConn, String filePath)
            throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.FILE_LIST,
                filePath));
        String[] files = (String[]) receiveResponseSilently(mdsConn)
                .getValue();

        return files;
    }

    /**
     * @param mdsConn
     * @param fileMD
     * @return true if file exists and max chunkSize was not set before
     * @throws IOException
     */
    public static boolean fileSetMaxChunkSize(Connection mdsConn,
            FileMetaData fileMD) throws IOException {
        mdsConn.sendRequest(new CommandRequest(
                ISignalsMDS.FILE_SET_MAX_CHUNK_SIZE, fileMD));
        boolean success = receiveResponseSilently(mdsConn)
                .getValueAsBoolean();

        return success;
    }

    // -------------node-related--------------------------
    /**
     * @param mdsConn
     * @param freeDiscSpace
     * @return a node with the given amount of free discspace or null if none
     *         exist
     * @throws IOException
     */
    public static NodeMetaData nodeGetAFree(Connection mdsConn,
            long freeDiscSpace) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.NODE_GET_A_FREE,
                new Long(freeDiscSpace)));
        NodeMetaData node = (NodeMetaData) receiveResponseSilently(mdsConn)
                .getValue();

        return node;
    }

    /**
     * @param mdsConn
     * @param chunkID
     * @return all nodes which has the chunk denotes by the given chunkID
     * @throws IOException
     */
    public static NodeMetaData[] nodeGetFromChunk(Connection mdsConn,
            String chunkID) throws IOException {
        mdsConn.sendRequest(new CommandRequest(
                ISignalsMDS.NODE_GET_FROM_CHUNK, chunkID));
        NodeMetaData[] nodes = (NodeMetaData[]) receiveResponseSilently(
                mdsConn).getValue();

        return nodes;
    }

    /**
     * @param mdsConn
     * @param nodeMDFrom
     * @param fileMDs
     * @throws IOException
     */
    public static void nodeInitFiles(Connection mdsConn,
            NodeMetaData nodeMDFrom, FileMetaData[] fileMDs) throws IOException {
        CommandRequest request = new CommandRequest(
                ISignalsMDS.NODE_INIT_FILES, fileMDs);
        request.setFrom(nodeMDFrom);
        mdsConn.sendRequest(request);
        receiveResponseSilently(mdsConn);
    }

    /**
     * @param mdsConn
     * @param nodeMDFrom
     * @param chunkMDs
     * @return all ChunkMetaData's could not associated with a file
     * @throws IOException
     */
    public static ChunkMetaData[] nodeInitChunks(Connection mdsConn,
            NodeMetaData nodeMDFrom, ChunkMetaData[] chunkMDs)
            throws IOException {
        CommandRequest request = new CommandRequest(
                ISignalsMDS.NODE_INIT_CHUNKS, chunkMDs);
        request.setFrom(nodeMDFrom);
        mdsConn.sendRequest(request);
        ChunkMetaData[] chunkMDsToDelete = (ChunkMetaData[]) receiveResponseSilently(
                mdsConn).getValue();

        return chunkMDsToDelete;
    }

    // ------------chunk-related--------------------------
    /**
     * @param mdsConn
     * @param nodeMD
     * @param chunkMD
     * @throws IOException
     * @throws CommandException
     */
    public static void chunkWritten(Connection mdsConn, NodeMetaData nodeMD,
            ChunkMetaData chunkMD) throws IOException, CommandException {
        CommandRequest request = new CommandRequest(ISignalsMDS.CHUNK_WRITTEN,
                chunkMD);
        request.setFrom(nodeMD);
        mdsConn.sendRequest(request);
        mdsConn.receiveResponse();
    }

    // -------------different-----------------------------
    /**
     * @param mdsConn
     * @return an abitrary id generated by metadataserver
     * @throws IOException
     */
    public static String getAnId(Connection mdsConn) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.GET_AN_ID));
        String id = (String) receiveResponseSilently(mdsConn).getValue();

        return id;
    }

    /**
     * @param mdsConn
     * @param nodeMD
     * @return true if node is known from mds, false if not
     * @throws IOException
     */
    public static boolean sendLifeSign(Connection mdsConn, NodeMetaData nodeMD)
            throws IOException {
        mdsConn
                .sendRequest(new CommandRequest(ISignalsMDS.LIFE_SIGN, nodeMD));
        boolean known = receiveResponseSilently(mdsConn).getValueAsBoolean();

        return known;
    }

    /**
     * @param mdsConn
     * @return the (remote) ip address of the client
     * @throws IOException
     */
    public static String getIp(Connection mdsConn) throws IOException {
        mdsConn.sendRequest(new CommandRequest(ISignalsMDS.GET_IP));
        String ipAddress = (String) receiveResponseSilently(mdsConn)
                .getValue();

        return ipAddress;
    }

}

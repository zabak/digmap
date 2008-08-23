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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/ServerFaceDNS.java,v $
 */

package net.weta.dfs.com;

import java.io.IOException;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;

/**
 * You could use the DNServerFace for communication with DataNodeServer. All you
 * need is an Connection which is focused on one DataNodeServer. <br/>
 * 
 * To know which value each request type needs refer to ClientHandlerDNS.
 * 
 * <br/><br/>created on 02.05.2005
 * 
 * @version $Revision: 1.2 $
 * @see net.weta.dfs.server.dns.ClientHandlerDNS
 * @see net.weta.dfs.com.Connection
 */
public class ServerFaceDNS extends AbstractServerFace {

    /**
     * Opens a connection to write a chunk with Connection.write..() methods.
     * 
     * @param dnsConn
     * @param fileMD
     * @param chunkMD
     * @throws IOException
     * @throws CommandException
     */
    public static void openWriteConnection(Connection dnsConn,
            FileMetaData fileMD, ChunkMetaData chunkMD) throws IOException,
            CommandException {
        dnsConn
                .sendRequest(new CommandRequest(ISignalsDNS.CHUNK_WRITE,
                        chunkMD));
        dnsConn
                .sendRequest(new CommandRequest(ISignalsDNS.CHUNK_WRITE, fileMD));
        dnsConn.receiveResponse();
    }

    /**
     * Close the connection for Connection.write..() methods.
     * 
     * @param dnsConn
     * @throws IOException
     * @throws CommandException
     */
    public static void closeWriteConnection(Connection dnsConn)
            throws IOException, CommandException {
        dnsConn.writeInt(0);
        dnsConn.receiveResponse();
    }

    /**
     * Opens a connection to read a chunk with Connection.read..() methods.
     * 
     * @param dnsConn
     * @param chunkMD
     * @return the length of the chunkfile
     * @throws IOException
     * @throws CommandException
     */
    public static long openReadConnection(Connection dnsConn,
            ChunkMetaData chunkMD) throws IOException, CommandException {
        // TODO really need to return chunk size (why not trust mds)?
        dnsConn
                .sendRequest(new CommandRequest(ISignalsDNS.CHUNK_READ, chunkMD));
        long chunkSize = dnsConn.receiveResponse().getValueAsLong();
        return chunkSize;
    }

    /**
     * @param dnsConn
     * @param chunkMD
     * @return true if chunk exists and deleting was successful
     * @throws IOException
     */
    public static boolean deleteChunk(Connection dnsConn, ChunkMetaData chunkMD)
            throws IOException {
        dnsConn.sendRequest(new CommandRequest(ISignalsDNS.CHUNK_DELETE,
                chunkMD));
        boolean success = receiveResponseSilently(dnsConn).getValueAsBoolean();
        return success;
    }

    /**
     * @param dnsConn
     * @return the NodeMetaData of the node the client is connected to
     * @throws IOException
     */
    public static NodeMetaData getNodeInformation(Connection dnsConn)
            throws IOException {
        dnsConn
                .sendRequest(new CommandRequest(
                        ISignalsDNS.NODE_GET_INFORMATION));
        NodeMetaData nodeMD = (NodeMetaData) receiveResponseSilently(dnsConn)
                .getValue();
        return nodeMD;
    }
}

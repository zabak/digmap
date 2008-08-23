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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/ClientHandlerMDS.java,v $
 */

package net.weta.dfs.server.mds;

import java.io.IOException;
import java.net.Socket;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.CommandResult;
import net.weta.dfs.com.ICommandResponse;
import net.weta.dfs.com.ISignalsMDS;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceDNS;
import net.weta.dfs.server.ClientHandler;
import net.weta.dfs.server.Server;
import net.weta.dfs.util.PathUtil;

/**
 * Handles the communication with a client or a datanodeserver to process
 * meta-data flow.
 * 
 * <br/><br/>created on 12.01.2005
 * 
 * @version $Revision: 1.2 $
 */
public class ClientHandlerMDS extends ClientHandler {

    private volatile MetaDataServer fServer;

    /**
     * @param server
     * @param aClientSocket
     */
    public ClientHandlerMDS(Server server, Socket aClientSocket) {
        super(server, aClientSocket, ISignalsMDS.class);
        this.fServer = (MetaDataServer) server;
    }

    protected ICommandResponse processRequest(CommandRequest request) {
        ICommandResponse response;
        try {
            switch (request.getType()) {
            case ISignalsMDS.FILE_CREATE:
                response = processFileCreate(request);
                break;
            case ISignalsMDS.FILE_DELETE:
                response = processFileDelete(request);
                break;
            case ISignalsMDS.FILE_GET_ID:
                response = processFileGetId(request);
                break;
            case ISignalsMDS.FILE_EXISTS:
                response = processFileExists(request);
                break;
            case ISignalsMDS.FILE_IS_DIRECTORY:
                response = processFileIsDirectory(request);
                break;
            case ISignalsMDS.FILE_IS_FILE:
                response = processFileIsFile(request);
                break;
            case ISignalsMDS.FILE_GET_LENGTH:
                response = processFileGetLength(request);
                break;
            case ISignalsMDS.FILE_MK_DIRS:
                response = processMkDirs(request);
                break;
            case ISignalsMDS.FILE_LIST:
                response = processFileList(request);
                break;
            case ISignalsMDS.FILE_GET_CHUNKS:
                response = processFileGetChunks(request);
                break;
            case ISignalsMDS.FILE_SET_MAX_CHUNK_SIZE:
                response = processFileSetMaxChunkSize(request);
                break;
            // -------------node-related--------------------------
            case ISignalsMDS.NODE_GET_FROM_CHUNK:
                response = processNodesGetFromChunk(request);
                break;
            case ISignalsMDS.NODE_INIT_FILES:
                response = processNodeInitFiles(request);
                break;
            case ISignalsMDS.NODE_INIT_CHUNKS:
                response = processNodeInitChunks(request);
                break;

            case ISignalsMDS.NODE_GET_A_FREE:
                response = processNodeGetAFree(request);
                break;
            // ------------chunk-related--------------------------
            case ISignalsMDS.CHUNK_WRITTEN:
                response = processChunkWritten(request);
                break;
            // -------------different-----------------------------
            case ISignalsMDS.LIFE_SIGN:
                response = processLifeSign(request);
                break;
            case ISignalsMDS.GET_AN_ID:
                response = processGetAnId();
                break;
            case ISignalsMDS.GET_IP:
                response = processGetIp();
                break;
            default:
                fLogger.error("Unknown request: " + request.getType());
                response = new CommandException("unknown request");
                break;
            }
        } catch (Exception e) {
            fLogger
                    .error("an exception encountered by processing a request",
                            e);
            response = new CommandException(e);
        }
        return response;
    }

    // -------------file-related--------------------------
    private ICommandResponse processFileCreate(CommandRequest request) {
        String path = (String) request.getValue();
        String parentPath = PathUtil.getParentDirectoryPath(path);
        if (!this.fServer.getFileSystem().isDirectory(parentPath)) return new CommandException("parent directory does not exist");
        boolean notExistsBefore = this.fServer.getFileSystem().createFile(path, MetaDataServer.getId());
        return new CommandResult(notExistsBefore);
    }

    private ICommandResponse processFileDelete(CommandRequest request)
            throws IOException {
        String filePath = (String) request.getValue();

        boolean exist = false;
        if (this.fServer.getFileSystem().isDirectory(filePath)) {
            exist = this.fServer.getFileSystem().delete(filePath);
        } else {
            ChunkMetaData[] chunkMDs = this.fServer.getFileSystem()
                    .getFileChunks(filePath);
            exist = this.fServer.getFileSystem().delete(filePath);
            if (chunkMDs != null && chunkMDs.length > 0) {
                sendDeleteChunks(chunkMDs);
                for (int i = 0; i < chunkMDs.length; i++) {
                    this.fServer.getNodeManager().removeChunk(
                            chunkMDs[i].getId());
                }
            }
        }
        return new CommandResult(exist);
    }

    private void sendDeleteChunks(ChunkMetaData[] chunkMDs) throws IOException {
        // TODO optimisation(not sent 2 requests to one node)
        for (int i = 0; i < chunkMDs.length; i++) {
            NodeMetaData[] nodeMDs = this.fServer.getNodeManager()
                    .getChunkNodes(chunkMDs[i].getId());
            for (int j = 0; j < nodeMDs.length; j++) {
                Connection dnsClient = new Connection(nodeMDs[j]
                        .getHostAddress(), nodeMDs[j].getPort());
                boolean success;
                try {
                    success = ServerFaceDNS.deleteChunk(dnsClient, chunkMDs[i]);
                } catch (Exception e) {
                    success = false;
                } finally {
                    dnsClient.close();
                }
                if (!success) {
                    // TODO what if fails, refer to WETADFS-58
                    fLogger.error("dns could not delete chunk "
                            + chunkMDs[i].getId());
                }
            }
        }
    }

    private ICommandResponse processFileExists(CommandRequest request) {
        String path = (String) request.getValue();

        boolean exists = this.fServer.getFileSystem().isFile(path)
                || this.fServer.getFileSystem().isDirectory(path);

        return new CommandResult(exists);
    }

    private ICommandResponse processFileIsFile(CommandRequest request) {
        String path = (String) request.getValue();
        boolean isFile = this.fServer.getFileSystem().isFile(path);

        return new CommandResult(isFile);
    }

    private ICommandResponse processFileIsDirectory(CommandRequest request) {
        String path = (String) request.getValue();
        boolean isDir = this.fServer.getFileSystem().isDirectory(path);

        return new CommandResult(isDir);
    }

    /**
     * @param request
     * @return The CommandResponse object with file length.
     */
    private ICommandResponse processFileGetLength(CommandRequest request) {
        String filePath = (String) request.getValue();
        long length = this.fServer.getFileSystem().getFileLength(filePath);

        return new CommandResult(length);
    }

    private ICommandResponse processFileGetId(CommandRequest request) {
        String path = (String) request.getValue();
        String fileId = this.fServer.getFileSystem().getFileId(path);

        return new CommandResult(fileId);
    }

    private ICommandResponse processFileGetChunks(CommandRequest request) {
        String path = (String) request.getValue();
        ChunkMetaData[] chunkMDs = this.fServer.getFileSystem().getFileChunks(
                path);
        return new CommandResult(chunkMDs);
    }

    private ICommandResponse processMkDirs(CommandRequest request) {
        String path = (String) request.getValue();
        boolean success = this.fServer.getFileSystem().createDirectories(path);
        return new CommandResult(success);
    }

    private ICommandResponse processFileList(CommandRequest request) {
        String path = (String) request.getValue();
        String[] content = this.fServer.getFileSystem().getDirectoryContent(path);
        return new CommandResult(content);
    }

    /**
     * @param request
     * @return The response as a ComanmdResonse object
     */
    private ICommandResponse processFileSetMaxChunkSize(CommandRequest request) {
        FileMetaData fileMD = (FileMetaData) request.getValue();
        boolean success = this.fServer.getFileSystem().setMaxChunkSize(
                fileMD.getFilePath(), fileMD.getMaxChunkSize());

        return new CommandResult(success);
    }

    // -------------node-related--------------------------
    private ICommandResponse processNodeGetAFree(CommandRequest request) {
        Long reqFreeSpace = (Long) request.getValue();
        NodeMetaData nodeMD = this.fServer.getNodeManager().getFreeNode(
                reqFreeSpace.longValue());

        return new CommandResult(nodeMD);
    }

    private ICommandResponse processNodesGetFromChunk(CommandRequest request) {
        NodeMetaData[] chunkNodes = this.fServer.getNodeManager()
                .getChunkNodes((String) request.getValue());

        return new CommandResult(chunkNodes);
    }

    private ICommandResponse processNodeInitFiles(CommandRequest request) {
        NodeMetaData nodeMD = request.getFrom();

        FileMetaData[] files = (FileMetaData[]) request.getValue();
        this.fServer.processFileMetaData(nodeMD, files);
        fLogger.info("init FileMetaData from " + nodeMD.getId());
        return new CommandResult();
    }

    private ICommandResponse processNodeInitChunks(CommandRequest request) {
        NodeMetaData nodeMD = request.getFrom();

        ChunkMetaData[] chunks = (ChunkMetaData[]) request.getValue();
        ChunkMetaData[] unAssChunks = this.fServer.processChunkMetaData(nodeMD,
                chunks);
        fLogger.info("init ChunkMetaData from " + nodeMD.getId());
        return new CommandResult(unAssChunks);
    }

    // ------------chunk-related--------------------------
    private ICommandResponse processChunkWritten(CommandRequest request) {
        ChunkMetaData chunkMd = (ChunkMetaData) request.getValue();
        String filePath = this.fServer.getFileSystem().getFilePath(
                chunkMd.getFileId());

        if ((filePath == null)
                || !(this.fServer.getFileSystem().addChunkToFile(chunkMd,
                        filePath)))
            return new CommandException("file does not exist");

        NodeMetaData nodeMD = request.getFrom();
        nodeMD.setLastLiveSign(System.currentTimeMillis());
        this.fServer.getNodeManager().updateNode(nodeMD);
        long maxChunkSize = this.fServer.getFileSystem().getMaxChunkSize(
                filePath);
        this.fServer.getNodeManager().addNodeToChunk(chunkMd.getId(),
                maxChunkSize, nodeMD);

        return new CommandResult();
    }

    // -------------different-----------------------------
    private ICommandResponse processLifeSign(CommandRequest request) {
        NodeMetaData nodeMD = (NodeMetaData) request.getValue();
        nodeMD.setLastLiveSign(System.currentTimeMillis());
        boolean known = this.fServer.getNodeManager().updateNode(nodeMD);

        return new CommandResult(known);
    }

    private ICommandResponse processGetIp() {
        String clientAddress = this.fClientSocket.getInetAddress()
                .getHostAddress();
        return new CommandResult(clientAddress);
    }

    private ICommandResponse processGetAnId() {
        return new CommandResult(MetaDataServer.getId());
    }

}
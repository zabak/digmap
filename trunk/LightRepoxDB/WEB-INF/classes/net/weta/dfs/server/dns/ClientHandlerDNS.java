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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/dns/ClientHandlerDNS.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.IOException;
import java.net.Socket;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.CommandResult;
import net.weta.dfs.com.ICommandResponse;
import net.weta.dfs.com.ISignalsDNS;
import net.weta.dfs.server.ClientHandler;
import net.weta.dfs.server.Server;

/**
 * Handles the communication with a client. Writes, reads chunks or sends status
 * information
 * 
 * <br/><br/>created on 14.01.2005
 * 
 * @version $Revision: 1.2 $
 */
public class ClientHandlerDNS extends ClientHandler {

    private DataNodeServer fServer;

    /**
     * @param server
     * @param aClientSocket
     */
    public ClientHandlerDNS(Server server, Socket aClientSocket) {
        super(server, aClientSocket, ISignalsDNS.class);
        this.fServer = (DataNodeServer) server;
    }

    protected ICommandResponse processRequest(CommandRequest request) {
        ICommandResponse response;
        try {
            switch (request.getType()) {
            case ISignalsDNS.CHUNK_WRITE:
                response = processWriteChunk(request);
                break;
            case ISignalsDNS.CHUNK_READ:
                response = processReadChunk(request);
                break;
            case ISignalsDNS.CHUNK_DELETE:
                response = processDeleteChunk(request);
                break;
            case ISignalsDNS.NODE_GET_INFORMATION:
                response = processGetNodeInformation();
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

    private ICommandResponse processWriteChunk(CommandRequest request)
            throws IOException, ClassNotFoundException {
        ChunkMetaData chunkMD = (ChunkMetaData) request.getValue();
        request = (CommandRequest) this.fOiStream.readObject();
        FileMetaData fileMD = (FileMetaData) request.getValue();

        if (fileMD.getMaxChunkSize() > this.fServer.getChunkManager()
                .getFreeDiskSpace())
            return new CommandException("not enough disc space");
        if (fileMD.getMaxChunkSize() == 0)
             return new CommandException("max chunk size not set");
        sendResponse(new CommandResult());

        try {
            long length = this.fServer.getChunkManager().writeChunk(
                    this.fClientSocket.getInputStream(), fileMD, chunkMD);
            chunkMD.setSize(length);
        } catch (IOException e) {
            try {
                this.fServer.getChunkManager().deleteChunk(chunkMD);
            } catch (IOException e1) {
                fLogger.error("failed to delete failed written chunk", e);
                // TODO memo for later deletion ?
            }
            throw e;
        }
        try {
            this.fServer.sendChunkWritten(chunkMD);
        } catch (CommandException e) {
            fLogger.error("failed to send CHUNK_WRITTEN", e);
            // TODO memo for later notification?
        }

        return new CommandResult();
    }

    private ICommandResponse processReadChunk(CommandRequest request)
            throws IOException {

        ChunkMetaData chunkMD = (ChunkMetaData) request.getValue();
        long size = this.fServer.getChunkManager().getChunkSize(chunkMD);
        sendResponse(new CommandResult(size));
        this.fServer.getChunkManager().readChunk(
                this.fClientSocket.getOutputStream(), chunkMD);

        // finish connection
        this.fClientSocket.close();
        return new CommandResult();

        /**
         * Don't send this Response. We should send the file content as an
         * object.
         */
        // sendResponse(CommandResponse.OK,null);
    }

    private ICommandResponse processDeleteChunk(CommandRequest request)
            throws IOException {
        ChunkMetaData chunkMD = (ChunkMetaData) request.getValue();
        boolean existsBefore = this.fServer.getChunkManager().deleteChunk(
                chunkMD);

        return new CommandResult(existsBefore);
    }

    private ICommandResponse processGetNodeInformation() {
        return new CommandResult(this.fServer.getNodeMD());
    }

    private void sendResponse(ICommandResponse response) {
        try {
            this.fOoStream.writeObject(response);
            this.fOoStream.flush();
            fLogger.debug("outgoing response - type: " + response.asString());
        } catch (IOException e) {
            fLogger.error("cannot send response", e);
        }
    }
}
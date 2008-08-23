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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/ClientHandler.java,v $
 */

package net.weta.dfs.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.ICommandResponse;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * A daemon thread that handle the connection to a client.
 * 
 * <br/><br/>created on 12.01.2005
 * 
 * @version $Revision: 1.1 $
 */
public abstract class ClientHandler extends Thread {

    protected static Category fLogger = Logger.getLogger(ClientHandler.class);

    protected ObjectInputStream fOiStream;

    protected ObjectOutputStream fOoStream;

    protected Socket fClientSocket;

    private Server fPortListener;

    protected Class fServerSignals;

    /**
     * Creates a ClientHandler and starts the thread. Bounds
     * fInputStream and fOutputStream. Should be called from all subclasses.
     * 
     * @param portListener
     * @param aClientSocket
     * @param serverSignals
     */
    public ClientHandler(Server portListener, Socket aClientSocket,
            Class serverSignals) {
        this.fPortListener = portListener;
        this.fClientSocket = aClientSocket;
        this.fServerSignals = serverSignals;

        this.setName(this.getClass().getName());
        this.setDaemon(true);
    }

    /**
     * 
     * @return the id of the client the connection is bound to
     */
    public String getClientId() {
        return this.fClientSocket.getInetAddress().getHostAddress() + ":"
                + this.fClientSocket.getPort();
    }

    /**
     * Reads a CommandRequest from inputStream of client socket, calls
     * proccessRequest and writes the CommandResponse back to outputStream of
     * client socket.
     */
    public final void run() {
        try {
            this.fOiStream = new ObjectInputStream(this.fClientSocket
                    .getInputStream());
            this.fOoStream = new ObjectOutputStream(this.fClientSocket
                    .getOutputStream());
            while (this.fClientSocket.isConnected()) {
                CommandRequest request = (CommandRequest) this.fOiStream
                        .readObject();
                if (fLogger.isDebugEnabled())
                    fLogger.debug("incoming request - type: "
                            + getSignalName(request.getType()));

                ICommandResponse response = processRequest(request);
                if (fLogger.isDebugEnabled())
                    fLogger.debug("outgoing response : " + response.asString());

                this.fOoStream.writeObject(response);
                this.fOoStream.flush();
            }
        } catch (EOFException e) {
            fLogger.debug("handler from " + getClientId()
                    + ": client closed connection");
        } catch (SocketTimeoutException e) {
            fLogger.debug("handler from client" + getClientId()
                    + ": connection timeout");
        } catch (SocketException e) {
            fLogger.debug("handler from client " + getClientId()
                    + ": client crashed");
        } catch (Exception e) {
            if (e instanceof InterruptedException)
                fLogger.debug("handler from " + getClientId()
                        + ": gets shut down");
            else
                fLogger.error("handler from " + getClientId() + " "
                        + e.getLocalizedMessage(), e);
        } finally {
            interrupt();
            this.fPortListener.finish(this);
        }
    }

    protected final String getSignalName(int signal) {
        Field[] fields = this.fServerSignals.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                if (fields[i].getInt("") == signal)
                    return fields[i].getName();
            } catch (Exception e) {
                fLogger.error("exception by determining signal name", e);
            }
        }
        return "UNKNOWN";
    }

    /**
     * This function is called, if an CommandRequest arrives.
     * 
     * @param request
     * @return a CommandResponse
     */
    protected abstract ICommandResponse processRequest(CommandRequest request);

    /**
     * 
     */
    public void interrupt() {
        super.interrupt();
        try {
            this.fClientSocket.close();
        } catch (IOException e) {
            fLogger.error(e.getLocalizedMessage(), e);
        }
    }
}
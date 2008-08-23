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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/Server.java,v $
 */

package net.weta.dfs.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * This class Server contains basic funtionality for any server.
 * 
 * Creating a server instance will not claim any port, this will happen through
 * startServer() method.
 * 
 * <br/><br/>created on 17.05.2005
 * 
 * @version $Revision: 1.5 $
 */
public class Server implements Runnable {

    protected static Category fLogger = Logger.getLogger(Server.class);

    private Thread fServerThread;

    private String fName;

    private String fRemoteIp;

    private int fPort;

    private ServerSocket fServerSocket;

    private Class fHandlerClass;

    private HashMap fClientThreads = new HashMap();

    /**
     * Creates a server instance under given ipAddress and port.
     * 
     * @param ipAddress
     *            ,the remote one
     * @param port
     * @param clientHandler
     *            ,a thread class handling client requests
     */
    public Server(String ipAddress, int port, Class clientHandler) {
        this.fRemoteIp = ipAddress;
        this.fPort = port;
        this.fHandlerClass = clientHandler;
        this.fName = this.getClass().getName().substring(
                this.getClass().getName().lastIndexOf(".") + 1);
    }

    /**
     * Creates a server instance under given port. <p/>
     * 
     * Note: The ipAddress will be determined, but on some OS the method
     * getIpAddress() will give back a local loopback address instead of the
     * remote ip the clients using.
     * 
     * @param port
     * @param clientHandler
     * @throws IOException
     */
    public Server(int port, Class clientHandler) throws IOException {
        this(InetAddress.getLocalHost().getHostAddress(), port, clientHandler);
    }

    /**
     * Sets the server name. Deafult is the class name.
     * 
     * @param name
     */
    public void setName(String name) {
        this.fName = name;
    }

    /**
     * @return the name of the server
     */
    public String getName() {
        return this.fName;
    }

    /**
     * @return true if server is running and listens to its port
     */
    public synchronized boolean isRunning() {
        return this.fServerThread != null && this.fServerThread.isAlive();
    }

    /**
     * Starts the server.
     * 
     * @throws IOException
     */
    public synchronized void startServer() throws IOException {
        if (!isRunning()) {
            this.fServerSocket = new ServerSocket(this.fPort);
            this.fServerThread = new Thread(this);
            this.fServerThread.start();
            while (!this.fServerThread.isAlive())
                Thread.yield();
        }
    }

    /**
     * Stops the server.
     * 
     * @throws IOException
     */
    public synchronized void stopServer() throws IOException {
        if (isRunning()) {
            this.fServerThread.interrupt();
            this.fServerSocket.close();
            while (this.fServerThread.isAlive())
                Thread.yield();
            this.fServerThread = null;
            killAllClientThreads();
        }
    }

    /**
     * @return the port the server is listens to
     */
    public int getPort() {
        return this.fPort;
    }

    /**
     * Sets the ipAddress. This is useful since on some OS you get a local
     * loopback address by determining the ip.
     * 
     * @param ip
     */
    public void setIpAddress(String ip) {
        this.fRemoteIp = ip;
    }

    /**
     * @return the remote ip address of the server
     */
    public String getIpAddress() {
        return this.fRemoteIp;
    }

    /**
     * @param connection
     * @return true if ClientServerConnection maintained by this port listener
     */
    public synchronized boolean finish(ClientHandler connection) {
        return this.fClientThreads.remove(connection.getClientId()) != null;
    }

    /**
     * @return the size of active client threads
     */
    public int getClientThreadCount() {
        return this.fClientThreads.size();
    }

    /**
     * Interrupts all ClientServerConnection threads which were started by this
     * PortListener.
     * 
     */
    public synchronized void killAllClientThreads() {
        Collection clients = Collections
                .synchronizedCollection(this.fClientThreads.values());

        for (Iterator iter = clients.iterator(); iter.hasNext();) {
            ClientHandler clientConnection = (ClientHandler) iter.next();
            clientConnection.interrupt();
            iter.remove();
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        stopServer();
        super.finalize();
    }

    /**
     * Waits on connection of a client and starts the specified clientHandler
     * for every client.
     */
    public void run() {
        try {
            while (true) {
                Socket clientSocket = this.fServerSocket.accept();
                clientSocket.setSoTimeout(10000);
                fLogger.debug(this.getName()
                        + ": incomming new client connection from "
                        + clientSocket.getInetAddress().getHostAddress() + ":"
                        + clientSocket.getPort());

                Class[] argTypes = { Server.class, Socket.class };
                Constructor thread = this.fHandlerClass
                        .getConstructor(argTypes);
                Object[] argObjects = { this, clientSocket };
                ClientHandler clientConnection = (ClientHandler) thread
                        .newInstance(argObjects);
                clientConnection.start();

                if (this.fClientThreads.put(clientConnection.getClientId(),
                        clientConnection) != null) {
                    throw new IOException("2 connections to one address");
                }
            }
        } catch (SocketException e) {
            // normal shutdown
            fLogger.info(this.getName() + ": stopped");
        } catch (Exception e) {
            fLogger.error(e.getLocalizedMessage(), e);
        }
    }
}

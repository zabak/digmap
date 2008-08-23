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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/ServerTest.java,v $
 */

package net.weta.dfs.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;
import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.CommandResult;
import net.weta.dfs.testutil.LoopbackRequestHandler;

/**
 * TestServer
 * 
 * <br/><br/>created on 17.05.2005
 * 
 * @version $Revision: 1.3 $
 * 
 */
public class ServerTest extends TestCase {

    private String fIp;
    
    private int fPort=4000;

    protected void setUp() throws Exception {
        super.setUp();
        this.fIp = InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * @throws IOException
     */
    public void testConstructor() throws IOException {
        // without ip
        Server server = new Server(this.fPort, LoopbackRequestHandler.class);
        assertEquals(this.fIp, server.getIpAddress());
        assertEquals(this.fPort, server.getPort());

        assertEquals("Server", server.getName());
        assertFalse(server.isRunning());

        // with ip
        server = new Server("host", this.fPort, LoopbackRequestHandler.class);
        assertEquals("host", server.getIpAddress());
    }

    /**
     * @throws IOException
     */
    public void testStartStopServer() throws IOException {
        // port already used
        ServerSocket socket = new ServerSocket(this.fPort);
        try {
            new Server("host", socket.getLocalPort(),
                    LoopbackRequestHandler.class).startServer();
            fail("port is already used");
        } catch (IOException e) {
        }
        socket.close();

        Server server = new Server(this.fPort, LoopbackRequestHandler.class);
        // start server
        server.startServer();
        assertTrue(server.isRunning());
        assertEquals(0, server.getClientThreadCount());
        server.startServer();
        assertTrue(server.isRunning());

        // stop server
        server.stopServer();
        assertFalse(server.isRunning());
        server.stopServer();

        // start server again
        server.startServer();
        assertTrue(server.isRunning());
        assertEquals(0, server.getClientThreadCount());
        server.stopServer();
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public void testClientConnections() throws IOException, ClassNotFoundException,
            InterruptedException {
        // start server
        Server server = new Server(this.fPort, LoopbackRequestHandler.class);
        server.startServer();
        assertEquals(0, server.getClientThreadCount());

        // start client1
        Socket clientSocket1 = new Socket("127.0.0.1", this.fPort);
        Thread.sleep(200);
        assertEquals(1, server.getClientThreadCount());
        // start client2
        Socket clientSocket2 = new Socket("127.0.0.1", this.fPort);
        Thread.sleep(100);
        assertEquals(2, server.getClientThreadCount());

        // write requests
        CommandRequest request = new CommandRequest(8, new Integer(7));
        ObjectOutputStream ooStream1 = new ObjectOutputStream(clientSocket1
                .getOutputStream());
        ObjectOutputStream ooStream2 = new ObjectOutputStream(clientSocket2
                .getOutputStream());
        for (int i = 0; i < 23; i++) {
            ooStream1.writeObject(request);
            ooStream1.flush();
            ooStream2.writeObject(request);
            ooStream2.flush();
        }

        // receive Responses
        ObjectInputStream oiStream1 = new ObjectInputStream(clientSocket1
                .getInputStream());
        ObjectInputStream oiStream2 = new ObjectInputStream(clientSocket2
                .getInputStream());
        for (int i = 0; i < 23; i++) {
            CommandResult result = (CommandResult) oiStream1.readObject();
            assertEquals(request.getValue(), result.getValue());
            result = (CommandResult) oiStream2.readObject();
            assertEquals(request.getValue(), result.getValue());
        }

        clientSocket1.close();
        Thread.sleep(200);
        assertEquals(1, server.getClientThreadCount());
        server.stopServer();
        assertEquals(0, server.getClientThreadCount());
    }
}

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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/com/ConnectionTest.java,v $
 */

package net.weta.dfs.com;

import java.io.IOException;

import junit.framework.TestCase;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.Connection;
import net.weta.dfs.server.Server;
import net.weta.dfs.testutil.LoopbackRequestHandler;

/**
 * ConnectionTest comment
 * 
 * created on 28.01.2005
 * 
 * @author sg
 * @version $Revision: 1.1 $
 * 
 */
public class ConnectionTest extends TestCase {
    
    private Connection fConnection;

    private Server fServer;

    private int connects = 100;

    private int port = 30000;

    protected void setUp() throws Exception {
        super.setUp();
        this.fServer = new Server(this.port,
                LoopbackRequestHandler.class);
        this.fServer.setName("ConnectionTest");
        this.fServer.startServer();
        this.fConnection = new Connection("127.0.0.1", this.port);
    }

    protected void tearDown() throws Exception {
        this.fConnection.close();
        this.fServer.stopServer();
        super.tearDown();
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    public void testRequestResponseString() throws IOException,
            CommandException {
        // first let us try sending Strings
        for (int i = 0; i < this.connects; i++) {
            CommandRequest request = new CommandRequest(i, "value" + i);
            this.fConnection.sendRequest(request);
            assertEquals("value" + i, this.fConnection.receiveResponse().getValue());
        }
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    public void testRequestResponseNull() throws IOException, CommandException {
        CommandRequest request = new CommandRequest(1, null);
        this.fConnection.sendRequest(request);
        assertNull(this.fConnection.receiveResponse().getValue());
    }
}

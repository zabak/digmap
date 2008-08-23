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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/testutil/LoopbackRequestHandler.java,v $
 */

package net.weta.dfs.testutil;

import java.net.Socket;

import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.CommandResult;
import net.weta.dfs.com.ICommandResponse;
import net.weta.dfs.com.ISignalsMDS;
import net.weta.dfs.server.ClientHandler;
import net.weta.dfs.server.Server;

/**
 * 
 * TestUtil request handler which response on incoming CommandRequests with
 * CommandResult with same value as request value.
 * 
 * @version $Revision: 1.3 $
 * 
 */
public class LoopbackRequestHandler extends ClientHandler {

    /**
     * @param server
     * @param aClientSocket
     */
    public LoopbackRequestHandler(Server server,
            Socket aClientSocket) {
        super(server, aClientSocket,ISignalsMDS.class);
    }

    protected ICommandResponse processRequest(CommandRequest request) {
        return new CommandResult(request.getValue());
    }
}
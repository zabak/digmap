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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/AbstractServerFace.java,v $
 */

package net.weta.dfs.com;

import java.io.IOException;

import org.apache.log4j.Category;

import com.fourspaces.featherdb.utils.Logger;

/**
 * A ServerFace encapsulate communictaion with a server behind method calls.
 * 
 * <br/><br/>created on 11.05.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public abstract class AbstractServerFace {

    protected static Category fLogger = org.apache.log4j.Logger.getLogger(AbstractServerFace.class);

    /**
     * Should be used if its sure that no CommandException will be thrown.
     * 
     * @param connection
     * @return the received CommandResult
     * @throws IOException
     */
    protected static CommandResult receiveResponseSilently(Connection connection) throws IOException {
        CommandResult result = null;
        try {
            result = connection.receiveResponse();
        } catch (CommandException e) {
            // will not happen
            fLogger.fatal("this points to a programming mistake", e);
        }
        return result;
    }
}

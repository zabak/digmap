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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/CommandException.java,v $
 */

package net.weta.dfs.com;

/**
 * This exception may be thrown while an server processes a CommandRequest or it
 * may wrap an exception encountered on a server. The CommandException could
 * then be transported to client as CommandResponse.
 * 
 * <br/><br/>created on 09.05.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class CommandException extends Exception implements ICommandResponse {

    /**
     * @param message
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CommandException(Exception cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CommandException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * (non-Javadoc)
     * 
     * @see net.weta.dfs.com.ICommandResponse#asString()
     */
    public String asString() {
        return "CommandException:" + super.getLocalizedMessage();
    }
}

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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/CommandResult.java,v $
 */

package net.weta.dfs.com;

import java.io.Serializable;

/**
 * The CommandResult is the CommandResponse to a CommandRequest if no
 * CommandException is thrown while processing the CommandRequest.
 * 
 * Note: Often it occurs the the CommandResult contains no value. This is the
 * common way to express a similar behaviour like a void method.
 * 
 * <br/><br/>created on 09.05.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class CommandResult implements ICommandResponse {

    private Serializable fValue;

    /**
     * Constructs an CommandResult which carries no value with it. As a
     * CommandResponse the CommandResult itself acts as indice that the
     * CommandRequest was successful processed.(comparable to a void method)
     */
    public CommandResult() {
    }

    /**
     * @param value
     */
    public CommandResult(Serializable value) {
        this.fValue = value;
    }

    /**
     * The same as CommandResponse(new Boolean(value)).
     * 
     * @param value
     */
    public CommandResult(boolean value) {
        this.fValue = new Boolean(value);
    }

    /**
     * The same as CommandResponse(new Long(value)).
     * 
     * @param value
     */
    public CommandResult(long value) {
        this.fValue = new Long(value);
    }

    /**
     * @return the value
     */
    public Serializable getValue() {
        return this.fValue;
    }

    /**
     * Note: Assumes that the value is a boolean, otherwise a ClassCastException
     * will be thrown.
     * 
     * @return true or false
     */
    public boolean getValueAsBoolean() {
        return ((Boolean) this.fValue).booleanValue();
    }

    /**
     * Note: Assumes that the value is a boolean, otherwise a ClassCastException
     * will be thrown.
     * 
     * @return true or false
     */
    public long getValueAsLong() {
        return ((Long) this.fValue).longValue();
    }

    /**
     * (non-Javadoc)
     * 
     * @see net.weta.dfs.com.ICommandResponse#asString()
     */
    public String asString() {
        return "CommandResult:" + this.fValue;
    }
}

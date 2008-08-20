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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/CommandRequest.java,v $
 */

package net.weta.dfs.com;

import java.io.Serializable;

import net.weta.dfs.NodeMetaData;


/**
 * Container for a command request message containing a type and a value.
 * 
 * <br/><br/>created on 13.01.2005
 * 
 * @version $Revision: 1.1 $
 */
public class CommandRequest implements Serializable {

    private int fTyp;

    private Serializable fValue;

    private NodeMetaData fFrom;

    /**
     * 
     * @param type
     * @param value
     */
    public CommandRequest(int type, Serializable value) {
        this.fTyp = type;
        this.fValue = value;
    }

    /**
     * @param type
     */
    public CommandRequest(int type) {
        this(type, null);
    }

    /**
     * @return the value
     */
    public Serializable getValue() {
        return this.fValue;
    }

    /**
     * 
     * @param value
     */
    public void setValue(Serializable value) {
        this.fValue = value;
    }

    /**
     * @return the type
     */
    public int getType() {
        return this.fTyp;
    }

    /**
     * @param ipAddress
     *            The requestorIp to set.
     */
    public void setFrom(NodeMetaData ipAddress) {
        this.fFrom = ipAddress;
        /*
         * TODO refactor to server address or something ?
         * 
         * Why you don't call the getInetAddress() function?
         * 
         * Cause in moment we need the the dns-id which consists of ip(we could
         * extract) and server port(we could'nt extract). Possible refactorings
         * proposals: callerSeverPort(if existent) or callerId instead of
         * NodeMetaData-From.
         */
    }

    /**
     * @return Returns the requestorIp.
     */
    public NodeMetaData getFrom() {
        return this.fFrom;
    }
}

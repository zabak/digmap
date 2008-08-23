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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/ICommandResponse.java,v $
 */

package net.weta.dfs.com;

import java.io.Serializable;

/**
 * Container for a command response message containing a type and a value.
 * 
 * <br/><br/>created on 13.01.2005
 * 
 * @version $Revision: 1.1 $
 */
public interface ICommandResponse extends Serializable {

    /**
     * @return an expressive String representation of this CommandResponse
     */
    public String asString();
}

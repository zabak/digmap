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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/com/ISignalsDNS.java,v $
 */

package net.weta.dfs.com;

/**
 * ISignalsDNS holds the different request signals that can be processed by
 * DataNodeServer. Use them as types in CommandRequest objects.
 * 
 * To know which value each request type needs refer to ServerFaceDNS and
 * ClientHandlerDNS.
 * 
 * <br/><br/>created on 09.05.2005
 * 
 * @version $Revision: 1.2 $
 * @see net.weta.dfs.com.CommandRequest
 * @see net.weta.dfs.server.dns.ClientHandlerDNS
 */
public interface ISignalsDNS {

    /**
     * Request a chunk to write.
     */
    public static final int CHUNK_WRITE = 1;

    /**
     * Request a chunk to read.
     */
    public static final int CHUNK_READ = 2;

    /**
     * Request a chunk to delete.
     */
    public static final int CHUNK_DELETE = 3;

    /**
     * Request the NodeMetaData of the node.
     */
    public static final int NODE_GET_INFORMATION = 21;
}

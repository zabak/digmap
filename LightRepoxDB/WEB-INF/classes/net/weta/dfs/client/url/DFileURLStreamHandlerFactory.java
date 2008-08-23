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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/url/DFileURLStreamHandlerFactory.java,v $
 */

package net.weta.dfs.client.url;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * URLStreamHandlerFactory for {@link DFileURLStreamHandler }
 * 
 * <br/><br/>created on 09.08.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class DFileURLStreamHandlerFactory implements URLStreamHandlerFactory {
    // TODO the address and port of MDS are just workarounds till a valuable
    // client->mds routing strategy is choosen
    private static String fMDSAddress;

    private static int fMDSPort;

    /**
     * Registers the {@link DFileURLStreamHandlerFactory} so the protocol of
     * {@link DFileURLStreamHandler} could be used.
     * 
     * @param mdsAddress
     * @param mdsPort
     */
    public static void init(String mdsAddress, int mdsPort) {
        URL.setURLStreamHandlerFactory(new DFileURLStreamHandlerFactory());
        fMDSAddress = mdsAddress;
        fMDSPort = mdsPort;
    }

    /**
     * @return the ip of the MetaDataServer
     */
    public static String getMDSIp() {
        if (fMDSAddress == null)
            throw new IllegalStateException("Factory have not yet been initialised");
        return fMDSAddress;
    }

    /**
     * @return the port of the MetaDataServer
     */
    public static int getMDSPort() {
        if (fMDSAddress == null)
            throw new IllegalStateException("Factory have not yet been initialised");
        return fMDSPort;
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {

        if (protocol.equals(DFileURLStreamHandler.PROTOCOL_NAME)) {
            return new DFileURLStreamHandler();
        }

        return null;
    }
}

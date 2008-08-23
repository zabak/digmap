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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/url/DFileURLStreamHandler.java,v $
 */

package net.weta.dfs.client.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * URLStreamHandler for URL's pointing to a weta-dfs
 * {@link net.weta.dfs.client.DFile}.
 * 
 * <br/><br/>created on 09.08.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class DFileURLStreamHandler extends URLStreamHandler {

    /**
     * Protocolname of URL points to a {@link net.weta.dfs.client.DFile}
     */
    public final static String PROTOCOL_NAME = "dfile";

    protected URLConnection openConnection(URL u) throws IOException {

        return new DFileURLConnection(u);
    }
}

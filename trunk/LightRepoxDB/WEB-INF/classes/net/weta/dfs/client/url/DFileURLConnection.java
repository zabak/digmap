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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/url/DFileURLConnection.java,v $
 */

package net.weta.dfs.client.url;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import net.weta.dfs.client.DFile;
import net.weta.dfs.client.DFileInputStream;
import net.weta.dfs.client.DFileOutputStream;

/**
 * URLConnection for accessing a {@link net.weta.dfs.client.DFile}.
 * 
 * <br/><br/>created on 09.08.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class DFileURLConnection extends URLConnection {

    private DFile fDFile;

    protected DFileURLConnection(URL url) {
        super(url);
    }

    public void connect() throws IOException {
        if (this.connected) {
            return;
        }

        this.fDFile = new DFile(this.url.getFile(), DFileURLStreamHandlerFactory.getMDSIp(),
                DFileURLStreamHandlerFactory.getMDSPort());
        if (!this.fDFile.exists())
            throw new FileNotFoundException("File " + this.url.getFile() + " does not exists");
        this.connected = true;
    }

    public long getLastModified() {
        return this.fDFile.lastModified();
    }

    public long getDate() {
        return getLastModified();
    }

    public int getContentLength() {
        return 0;
        // TODO same problem like File with throwing exceptions
        // return (int) this.fDFile.length();
    }

    public String getContentType() {
        return getFileNameMap().getContentTypeFor(this.fDFile.getName());
    }

    public OutputStream getOutputStream() throws IOException {
        if (!this.connected)
            connect();

        // TODO how to specify chunk size
        return new DFileOutputStream(this.fDFile, 32);
    }

    public InputStream getInputStream() throws IOException {
        if (!this.connected)
            connect();

        return new DFileInputStream(this.fDFile);
    }
}

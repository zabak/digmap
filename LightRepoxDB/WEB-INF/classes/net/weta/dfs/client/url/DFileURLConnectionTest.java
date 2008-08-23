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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/client/url/DFileURLConnectionTest.java,v $
 */

package net.weta.dfs.client.url;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;
import net.weta.dfs.client.DFile;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.util.FileUtil;

/**
 * DFileURLConnectionTest
 * 
 * <br/><br/>created on 09.08.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class DFileURLConnectionTest extends TestCase {

    private MetaDataServer fMDS;

    protected void setUp() throws Exception {
        this.fMDS = new MetaDataServer(Configuration.getInstance());
        this.fMDS.startServer();
    }

    protected void tearDown() throws Exception {
        this.fMDS.stopServer();
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testInitFactory() throws IOException {
        try {
            new URL(DFileURLStreamHandler.PROTOCOL_NAME, "", "dir1/dir2/file");
            fail("protocol unknown, DFileURLStreamHandlerFactory not initialized");
        } catch (MalformedURLException e) {
            //
        }

        DFileURLStreamHandlerFactory.init(this.fMDS.getIpAddress(), this.fMDS.getPort());
        new URL("dfile", "", "dir1/dir2/file");
    }

    /**
     * @throws Exception
     */
    public void testInputOutputStream() throws Exception {
        String dirName = "dir";
        String file = dirName + "/" + "file.sh";
        URL url = new URL(DFileURLStreamHandler.PROTOCOL_NAME, "", file);
        URLConnection connection = url.openConnection();

        try {
            connection.connect();
            fail("file not exists");
        } catch (FileNotFoundException e) {
            // 
        }

        // create File
        DFile dFile = new DFile(file, this.fMDS.getIpAddress(), this.fMDS.getPort());
        assertTrue(dFile.getParentFile().mkdirs());
        assertTrue(dFile.createNewFile());

        connection.connect();
        new DataNodeServer(Configuration.getInstance()).startServer();
        OutputStream oStream = connection.getOutputStream();
        for (int i = 0; i < 100; i++) {
            oStream.write(i);
        }
        oStream.close();
        InputStream iStream = connection.getInputStream();
        assertEquals(100, iStream.available());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, iStream.read());
        }
        iStream.close();
        assertEquals(dFile.lastModified(), connection.getLastModified());
        dFile.delete();
    }
}

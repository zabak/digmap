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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/testutil/RemoteTestDFile.java,v $
 */

package net.weta.dfs.testutil;

import java.io.IOException;

import net.weta.dfs.client.DFile;
import net.weta.dfs.client.DFileInputStream;
import net.weta.dfs.client.DFileOutputStream;
import net.weta.dfs.config.Configuration;
import junit.framework.TestCase;

/**
 * Write the specified amount of bytes to the dfs. Doesn't start any servers,
 * assume that they are running.
 * 
 * <br/><br/>created on 17.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class RemoteTestDFile extends TestCase {

    private String fIp;

    private int fPort;

    private long fChunkSize;

    private long fSize = (long) (70.6 * 1024 * 1024);

    private String fFileName = "/usr/usersreallybigfile.txt";

    /**
     * @throws IOException
     */
    public RemoteTestDFile() throws IOException {
        Configuration config = Configuration.getInstance();
        this.fIp = config.getProperty(Configuration.META_DATA_SERVER_IP);
        this.fPort = config
                .getPropertyAsInt(Configuration.META_DATA_SERVER_PORT);
        this.fChunkSize = 64;
    }

    /**
     * @throws Exception
     */
    public void testWrite() throws Exception {
        DFile dFile = new DFile(this.fFileName, this.fIp, this.fPort);
        assertFalse(dFile.exists());

        dFile.getParentFile().mkdirs();
        DFileOutputStream outputStream = new DFileOutputStream(dFile,
                this.fChunkSize);

        for (long i = 0; i < this.fSize; i++) {
            outputStream.write(45);
        }
        outputStream.close();
        dFile.getMDSConnection().close();
        Thread.sleep(5000);
    }

    /**
     * @throws Exception
     */
    public void testRead() throws Exception {
        DFile dFile = new DFile(this.fFileName, this.fIp, this.fPort);
        DFileInputStream inputStream = new DFileInputStream(dFile);

        long i = 0;
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            i++;
            assertEquals(45, b);
        }
        inputStream.close();
        assertEquals(this.fSize, i);
        dFile.getMDSConnection().close();
    }

    /**
     * @throws IOException
     * 
     */
    public void testDelete() throws IOException {
        DFile dFile = new DFile(this.fFileName, this.fIp, this.fPort);
        assertTrue(dFile.delete());
        dFile.getMDSConnection().close();
    }
}

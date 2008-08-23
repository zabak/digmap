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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/config/ConfigurationTest.java,v $
 */

package net.weta.dfs.config;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * ConfigurationTest
 * 
 * <br/><br/>created on 25.06.2004
 * 
 * @version $Revision: 1.8 $
 */
public class ConfigurationTest extends TestCase {

    /**
     * @throws IOException
     */
    public void testGetInstance() throws IOException {
        assertNotNull(Configuration.getInstance());
    }

    /**
     */
    public void testReadFromDSF_HOME() {

        // fail cause wrong CONFIG_PATH
        System
                .setProperty(Configuration.DFS_CONFIG_PATH,
                        "DFS_CONFIG_PATH set");
        try {
            Configuration.getInstance().reset();
            fail();
        } catch (IOException e) {
            assertTrue(true);
        }

        // success read from classpath
        System.setProperty(Configuration.DFS_CONFIG_PATH, "");
        try {
            Configuration.getInstance().reset();
            assertNotNull(Configuration.getInstance());
        } catch (IOException e) {
            fail();
        }

        // success read from right CONFIG_PATH
        System.setProperty(Configuration.DFS_CONFIG_PATH, "./src/conf");
        try {
            Configuration.getInstance().reset();
            assertNotNull(Configuration.getInstance());
        } catch (IOException e) {
            fail();
        }
    }

    /**
     * @throws IOException
     */
    public void testGetProperty() throws IOException {
        String property = Configuration.getInstance().getProperty(
                Configuration.DATA_NODE_PORT);
        assertNotNull(property);
    }

    /**
     * @throws IOException
     */
    public void testGetProperties() throws IOException {
        String[] property = Configuration.getInstance().getProperties(
                Configuration.DATA_NODE_PORT);
        assertEquals(property.length, 1);
        assertNotNull(property);

    }
}
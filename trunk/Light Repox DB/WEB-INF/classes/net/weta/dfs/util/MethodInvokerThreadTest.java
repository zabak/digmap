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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/util/MethodInvokerThreadTest.java,v $
 */

package net.weta.dfs.util;

import net.weta.dfs.util.MethodInvokerThread;
import junit.framework.TestCase;

/**
 * MethodInvokerThreadTest
 * 
 * <br/><br/>created on 10.04.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class MethodInvokerThreadTest extends TestCase {

    private long fCallTime = 250;

    private int fInvokeCount = 0;

    private Integer fInvokeArgsValue;

    /**
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     */
    public void testInvokeMethod() throws SecurityException,
            NoSuchMethodException, InterruptedException {
        MethodInvokerThread callerThread;
        try {
            callerThread = new MethodInvokerThread(this, "WrongName",
                    this.fCallTime);
            fail();
        } catch (NoSuchMethodException e) {
        }

        callerThread = new MethodInvokerThread(this, "invoke", this.fCallTime);
        callerThread.setName("MethodInvokerThreadTest");
        callerThread.start();
        Thread.sleep(this.fCallTime * 2 + this.fCallTime / 2);
        callerThread.interrupt();

        assertEquals(2, this.fInvokeCount);
    }

    /**
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     */
    public void testInvokeMethodWithArgs() throws SecurityException,
            NoSuchMethodException, InterruptedException {
        MethodInvokerThread callerThread = new MethodInvokerThread(this,
                "invokeArgs", this.fCallTime, new Class[] { Integer.class },
                new Object[] { new Integer(8) });
        callerThread.setName("MethodInvokerThreadTest");
        callerThread.start();
        Thread.sleep(this.fCallTime + this.fCallTime / 2);
        callerThread.interrupt();

        assertEquals(new Integer(8), this.fInvokeArgsValue);
    }

    /**
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     */
    public void testStopSmooth() throws SecurityException,
            NoSuchMethodException, InterruptedException {
        MethodInvokerThread callerThread = new MethodInvokerThread(this,
                "invoke", this.fCallTime);
        callerThread.setName("MethodInvokerThreadTest");

        // call interrupt()
        callerThread.start();
        Thread.sleep(20);
        callerThread.interrupt();
        Thread.sleep(20);
        assertFalse(callerThread.isAlive());
        assertEquals(0, this.fInvokeCount);

        // call stopSmooth()
        callerThread = new MethodInvokerThread(this, "invoke", this.fCallTime);
        callerThread.start();
        Thread.sleep(20);
        callerThread.stopSmooth();
        Thread.sleep(20);
        assertTrue(callerThread.isAlive());
        Thread.sleep(this.fCallTime);
        assertFalse(callerThread.isAlive());
        assertEquals(1, this.fInvokeCount);
    }

    /**
     * 
     */
    public void invoke() {
        this.fInvokeCount++;
    }

    /**
     * @param argValue
     */
    public void invokeArgs(Integer argValue) {
        this.fInvokeArgsValue = argValue;
    }
}

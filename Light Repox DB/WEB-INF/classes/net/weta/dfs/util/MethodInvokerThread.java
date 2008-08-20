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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/util/MethodInvokerThread.java,v $
 */

package net.weta.dfs.util;

import java.lang.reflect.Method;

import org.apache.log4j.Category;

/**
 * Thread class which call a specific method on a specific object at given time
 * periods. Any raised exceptions by invoking the given method will stops this
 * thread.
 * 
 * <br/><br/>created on 10.04.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class MethodInvokerThread extends Thread {

    static Category fLogger = Category.getInstance(MethodInvokerThread.class
            .getName());

    private long fSleepMillis;

    private Object fCallee;

    private Method fMethod;

    private Object[] fArgs;

    private volatile boolean fRun;

    /**
     * In every <code>sleepMillis</code> the invoker thread will call the
     * method with name <code>methodName</code> on object <code>callee</code>
     * with the given arguments
     * <code>args<code/> from given types <code>paramTypes<code/>.
     * 
     * @param callee
     * @param methodName
     * @param paramTypes
     * @param args
     * @param sleepMillis
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public MethodInvokerThread(Object callee, String methodName,
            long sleepMillis, Class[] paramTypes, Object[] args)
            throws SecurityException, NoSuchMethodException {
        this.setDaemon(true);
        this.fCallee = callee;
        this.fMethod = callee.getClass().getMethod(methodName, paramTypes);
        this.fArgs = args;
        this.fSleepMillis = sleepMillis;
    }

    /**
     * In every <code>sleepMillis</code> the invoker thread will call the
     * method with name <code>methodName</code> on object <code>callee</code>.
     * 
     * @param callee
     * @param methodName
     * @param sleepMillis
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public MethodInvokerThread(Object callee, String methodName,
            long sleepMillis) throws SecurityException, NoSuchMethodException {
        this(callee, methodName, sleepMillis, null, null);
    }

    /**
     * 
     */
    public void run() {
        fLogger.debug("starting invoker thread " + this.getName()
                + " on object " + this.fCallee + " and method "
                + this.fMethod.getName());
        try {
            while (this.fRun) {
                Thread.sleep(this.fSleepMillis);
                fLogger.debug("invoker thread " + this.getName()
                        + " on object " + this.fCallee + " calling method "
                        + this.fMethod.getName());
                this.fMethod.invoke(this.fCallee, this.fArgs);
            }
        } catch (InterruptedException e) {
            fLogger.debug("stopping invoker thread " + this.getName()
                    + " on object " + this.fCallee + " and method "
                    + this.fMethod.getName());
        } catch (Exception e) {
            fLogger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Causes this thread to begin execution; the Java Virtual Machine calls the
     * run() method of this thread.
     */
    public synchronized void start() {
        this.fRun = true;
        super.start();
    }

    /**
     * Stops this thread in a smooth way(at beginning of run()-loop). If this
     * thread didn't stops after some time it smells like a deadlock situation.
     */
    public void stopSmooth() {
        this.fRun = false;
    }
}

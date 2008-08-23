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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/util/PathUtil.java,v $
 */

package net.weta.dfs.util;

import java.util.StringTokenizer;

/**
 * Util class for handling pathes which can be files or directories. Every valid
 * path starts with the path-seperator.
 * 
 * <br/><br/>created on 12.02.2005
 * 
 * @version $Revision: 1.1 $
 */
public class PathUtil {

    /**
     * Comment for <code>SEPERATOR</code>
     */
    public static final String SEPERATOR = "/";

    /**
     * Convert a '\'-seperated path to a '/'-seperated path and eleminates
     * multiple occurrences like '\\' or '///'.
     * 
     * @param path
     * @return the corrected path
     */
    public static String correctPath(String path) {
        // TODO filter special character?
        if (path.trim().equals(""))
            return SEPERATOR;

        if (path.equals(SEPERATOR) || path.equals("\\"))
            return SEPERATOR;

        StringTokenizer tokenizer = new StringTokenizer(path, SEPERATOR + "\\");
        StringBuffer buffer = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            buffer.append(SEPERATOR);
            buffer.append(tokenizer.nextToken());
        }

        return buffer.toString();
    }

    /**
     * Seperates the parent path fom a path.
     * 
     * @param path
     *            a correct path
     * @return the parent directory of a path
     */
    public static String getParentDirectoryPath(String path) {
        if (path.equals(SEPERATOR)) {
            return null;
        }

        int lastIndex = path.lastIndexOf(SEPERATOR);

        if ((lastIndex == -1) || (lastIndex == 0)) {
            return SEPERATOR;
        }

        return path.substring(0, lastIndex);
    }

    /**
     * Seperates the last token after a PathUtil.SEPERATOR from a path, so it
     * could be a file name or a directory name.
     * 
     * @param path
     *            a correct path
     * @return the file name or directory name
     */
    public static String getFileName(String path) {
        if (path.equals(SEPERATOR)) {
            return SEPERATOR;
        }

        return path.substring(path.lastIndexOf(SEPERATOR) + 1, path.length());
    }

    /**
     * Splites the given path in its directory and file components.
     * 
     * @param path
     * @return The array of strings computed by splitting this string around
     *         matches of the <code>SEPERATOR</code>.
     */
    public static String[] getPathTokens(String path) {
        return path.split(SEPERATOR);
    }

    /**
     * Concatenates 2 paths with the <code>SEPERATOR</code>. Removes all unnecessary
     * "/" or "\" before/after path2/path1.
     * 
     * @param path1
     * @param path2
     * @return The resulting path.
     */
    public static String concatPath(String path1, String path2) {
        path1 = path1.replaceFirst("(?m)(.*?)[/\\\\]*?$", "$1" + SEPERATOR);
        path2 = path2.replaceFirst("(?m)^[/\\\\]*(.*?)", "$1");

        return path1.concat(path2);
    }
}
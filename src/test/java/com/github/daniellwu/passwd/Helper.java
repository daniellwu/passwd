package com.github.daniellwu.passwd;

import java.io.File;

/**
 * helper function common across mutiple test classes
 */
public class Helper {

    /**
     * Given a file on the test resource classloader, return the absolute path
     * @param filename name of file
     * @return absolute path
     */
    public static String getPathFromResource(String filename) {
        ClassLoader classLoader = Helper.class.getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        return file.getAbsolutePath();
    }
}

/*

Copyright (c) 2012, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

import java.io.*;
import java.util.*;

public class Io implements Assertions {


    public static String getResourcePropertyValue(Class anchorClass, String propertiesFileRelativePath, String key) {
        Properties properties = getProperties(anchorClass, propertiesFileRelativePath);
        String version = properties.getProperty(key);
        assertions.assertNotNull(String.format("%s not found in %s", key, propertiesFileRelativePath), version);
        return version;
    }

    public static Properties getProperties(Class anchorClass, String propertiesFileRelativePath) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(propertiesFileRelativePath);
            if (inputStream == null) {
                throw new RuntimeException("Couldn't find resource \"" + propertiesFileRelativePath + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                return properties;
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getFileBytes(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            try {
                return readBytes(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024 * 1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead == buffer.length) {
            throw new RuntimeException("Buffer size exceeded");
        }
        byte[] returnBytes = new byte[bytesRead];
        for (int i = 0; i < returnBytes.length; i++) {
            returnBytes[i] = buffer[i];
        }
        return returnBytes;
    }

    public static String readStreamIntoString(InputStream inputStream) {
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                int character;
                while ((character = bufferedReader.read()) != -1) {
                    stringWriter.write(character);
                }
            } finally {
                bufferedReader.close();
            }
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHomeDirectoryConfigFileProperty(String filename, String property) throws IOException {
        String usersHomePath = System.getProperty("user.home");
        if (usersHomePath == null) {
            throw new RuntimeException("Couldn't get user.home system property");
        }
        File homedir = new File(usersHomePath);
        if (!homedir.exists()) {
            throw new RuntimeException("Couldn't find home directory for user (" + homedir.getAbsolutePath() + " does not exist)");
        }
        File configFile = new File(homedir, filename);
        if (!configFile.exists()) {
            throw new RuntimeException("Configuration file (" + configFile.getAbsolutePath() + ") does not exist)");
        }
        Properties properties = new Properties();
        FileReader reader = new FileReader(configFile);
        try {
            properties.load(reader);
        } finally {
            reader.close();
        }
        String propertyValue = properties.getProperty(property);
        if (propertyValue == null) {
            throw new RuntimeException(String.format("Configuration properties file (%s) does not contain \"%s\")", configFile.getAbsolutePath(), property));
        }
        return propertyValue;
    }

    public static byte[] getResourceFileBytes(Class anchorClass, String filename) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new RuntimeException("Couldn't find resource \"" + filename + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                return readBytes(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResourceFileString(Class anchorClass, String filename) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new RuntimeException("Couldn't find resource \"" + filename + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                return readStreamIntoString(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

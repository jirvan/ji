/*

Copyright (c) 2014, Jirvan Pty Ltd
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

package com.jirvan.csv;

import com.jirvan.lang.MessageException;
import com.jirvan.lang.SQLRuntimeException;
import com.jirvan.util.Strings;
import org.apache.commons.io.FileUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.jirvan.util.Assertions.*;
import static com.jirvan.util.Strings.*;

public class BaseCsvImporter {

    private LinkedHashMap<String, CsvFileImporter> csvFileImporterMap;

    protected BaseCsvImporter(CsvFileImporter... csvFileImporters) {
        assertTrue(csvFileImporters.length > 0, "At least one CsvFileImporter must be provided");
        this.csvFileImporterMap = new LinkedHashMap<>();
        for (CsvFileImporter csvFileImporter : csvFileImporters) {
            this.csvFileImporterMap.put(csvFileImporter.handlesFileWithName(), csvFileImporter);
        }
    }

    public void importFromZipFile(DataSource dataSource, File zipFile) {
        try (InputStream zipInputStream = new FileInputStream(zipFile)) {
            importFromZippedInputStream(dataSource, zipInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void importFromZippedInputStream(DataSource dataSource, InputStream zippedInputStream) {
        try {
            File tempDir = Files.createTempDirectory("BaseCsvImporter.importDataEtc").toFile();
            try {

                // Extract zippedInputStream to temp data directory
                ZipInputStream zipInputStream = new ZipInputStream(zippedInputStream);
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    if (!(string(zipEntry.getName()).isIn(".DS_Store", ".localized")
                          || zipEntry.getName().startsWith("__MACOSX"))) {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(tempDir, zipEntry.getName()));
                        try {
                            int len;
                            byte[] buffer = new byte[1024];
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fileOutputStream.write(buffer, 0, len);
                            }
                        } finally {
                            fileOutputStream.close();
                        }
                    }
                }

                // Import from the temp data directory
                importFromDataDirectory(dataSource, tempDir);

            } finally {
                FileUtils.deleteDirectory(tempDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void importFromDataDirectory(DataSource dataSource, File dataDir) {

        checkDataDirectory(dataDir);
        StringBuilder pendingOutput = new StringBuilder();
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            try {

                System.out.printf("Importing data\n");
                System.out.flush();
                for (Map.Entry<String, CsvFileImporter> entry : csvFileImporterMap.entrySet()) {
                    File csvFile = new File(dataDir, entry.getKey());
                    CsvFileImporter csvFileImporter = entry.getValue();
                    csvFileImporter.importFromCsvFile(pendingOutput,
                                                      connection,
                                                      csvFile);
                }

                connection.commit();
                connection.close();
            } catch (Throwable t) {
                connection.rollback();
                connection.close();
                throw t;
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        System.out.printf("%sFinished importing data\n\n", pendingOutput.toString());

    }

    protected interface CsvFileImporter {

        public String handlesFileWithName();

        public void importFromCsvFile(StringBuilder pendingOutput, Connection connection, File csvFile);

    }

    public static class SimpleCsvFileImporter implements CsvFileImporter {

        private String handlesFilesWithName;

        public SimpleCsvFileImporter(String handlesFilesWithName) {
            this.handlesFilesWithName = handlesFilesWithName;
        }

        public String handlesFileWithName() {
            return handlesFilesWithName;
        }

        public void importFromCsvFile(StringBuilder pendingOutput, Connection connection, File csvFile) {
            String tableName = csvFile.getName().replaceFirst("(?i)\\.csv$", "");
            long rows = CsvTableImporter.importFromFile(connection, tableName, csvFile);
            pendingOutput.append(String.format("  - uploaded %d %s\n", rows, tableName.replaceAll("_", "")));
        }
    }

    private void checkDataDirectory(File dataDir) {

        // Check directory exists
        assertNotNull(dataDir, "Data directory must not be null");
        try {
            assertFileExists(dataDir, String.format("Data directory \"%s\" does not exist", dataDir.getAbsolutePath()));
            assertIsDirectory(dataDir, String.format("\"%s\" is not a directory", dataDir.getAbsolutePath()));
        } catch (AssertionError e) {
            throw new MessageException(e.getMessage());
        }

        // Check directory contains the expected files (and no others)
        Set<String> csvFileNames = csvFileImporterMap.keySet();
        String[] filesPresent = dataDir.list();
        for (String filePresent : filesPresent) {
            if (!Strings.isIn(filePresent, csvFileNames)) {
                throw new MessageException(String.format("File \"%s\" is unexpected\n(expected one of: %s)",
                                                         filePresent, Strings.commaSpaceList(csvFileNames)));
            }
        }
        for (String expectedFile : csvFileNames) {
            if (!Strings.isIn(expectedFile, filesPresent)) {
                throw new MessageException(String.format("\"%s\" is missing", expectedFile));
            }
        }

    }

}

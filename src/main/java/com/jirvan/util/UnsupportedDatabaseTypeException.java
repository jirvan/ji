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

package com.jirvan.util;

public class UnsupportedDatabaseTypeException extends RuntimeException {

    public UnsupportedDatabaseTypeException(String databaseTypeName, DatabaseType[] supportedDatabaseTypes) {
        super(buildMessage(databaseTypeName, supportedDatabaseTypes));
    }

    public UnsupportedDatabaseTypeException(DatabaseType databaseType, DatabaseType[] supportedDatabaseTypes) {
        super(buildMessage(databaseType.getDatabaseProductName(), supportedDatabaseTypes));
    }

    private static String buildMessage(String databaseTypeName, DatabaseType[] supportedDatabaseTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("\"%s\" databases are not supported", databaseTypeName));
        for (int i = 0; i < supportedDatabaseTypes.length; i++) {
            DatabaseType supportedDatabaseType = supportedDatabaseTypes[i];
            if (i == 0) {
                stringBuilder.append(String.format("\nvalid databases are : \"%s\" (or \"%s\")\n", supportedDatabaseType.name(), supportedDatabaseType.getDatabaseProductName()));
            } else {
                stringBuilder.append(String.format(",\n                      \"%s\" (or \"%s\")\n", supportedDatabaseType.name(), supportedDatabaseType.getDatabaseProductName()));
            }

        }
        return stringBuilder.toString();
    }

}
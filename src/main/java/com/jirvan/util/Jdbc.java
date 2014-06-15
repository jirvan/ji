/*

Copyright (c) 2008,2009,2010,2011,2012,2013,2014 Jirvan Pty Ltd
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

import com.jirvan.lang.*;
import net.sourceforge.jtds.jdbcx.*;
import oracle.jdbc.pool.*;
import org.postgresql.ds.*;
import org.postgresql.ds.common.*;

import javax.sql.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

//import com.teradata.jdbc.*;

public class Jdbc {

    public static String parameterPlaceHolderString(Collection collection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object parameter : collection) {
            if (stringBuilder.length() > 0) stringBuilder.append(',');
            stringBuilder.append('?');
        }
        return stringBuilder.toString();
    }

    public static String parameterPlaceHolderString(Object... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object parameter : parameters) {
            if (stringBuilder.length() > 0) stringBuilder.append(',');
            stringBuilder.append('?');
        }
        return stringBuilder.toString();
    }

    public static <T> T[] merge(T firstItem, T[] theRest) {
        if (firstItem == null) {
            throw new RuntimeException("Jdbc.merge(T firstItem, T[] theRest): firstItem must not be null (if this is a possibility you should use merge(Class<?> arrayClass, T firstItem, T[] theRest) instead.");
        }
        return merge(firstItem.getClass(), firstItem, theRest);
    }

    public static <T> T[] merge(Class<?> arrayClass, T firstItem, T[] theRest) {
        @SuppressWarnings("unchecked")
        T[] mergedArray = (T[]) Array.newInstance(arrayClass, theRest.length + 1);
        mergedArray[0] = firstItem;
        for (int i = 0; i < theRest.length; i++) {
            mergedArray[i + 1] = theRest[i];
        }
        return mergedArray;
    }

    public static int queryForInt(Connection conn, String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            try {
                ResultSet rset = stmt.executeQuery();
                if (rset.next()) {
                    int returnValue = rset.getInt(1);
                    if (rset.next()) {
                        throw new RuntimeException("queryForInt returned more than one row (exactly one expected)");
                    }
                    return returnValue;
                } else {
                    throw new RuntimeException("queryForInt returned no rows (exactly one expected)");
                }
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static Object queryForObject(Connection conn, String sql) {
        return queryForObject(conn, sql, null);
    }

    public static Object queryForObject(Connection conn, String sql, Object[] parameterValues) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            try {
                if (parameterValues != null) {
                    for (int i = 0; i < parameterValues.length; i++) {
                        stmt.setObject(i + 1, parameterValues[i]);
                    }
                }
                ResultSet rset = stmt.executeQuery();
                if (rset.next()) {
                    Object returnValue = rset.getObject(1);
                    if (rset.next()) {
                        throw new RuntimeException("queryForObject returned more than one row (exactly one expected)");
                    }
                    return returnValue;
                } else {
                    throw new RuntimeException("queryForObject returned no rows (exactly one expected)");
                }
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static void closeIgnoringExceptions(Statement statement) {
        try {
            statement.close();
        } catch (Throwable t) {
        }
    }

    public static void closeIgnoringExceptions(Connection connection) {
        try {
            connection.close();
        } catch (Throwable t) {
        }
    }

    public static void closeIgnoringExceptions(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (Throwable t) {
        }
    }

    public static Connection getPostgresConnection(String connectString) throws SQLException {
        return getPostgresDataSource((String) connectString).getConnection();
    }

    public static Connection getOracleConnection(String connectString) throws SQLException {
        DataSource dataSource = getOracleDataSource(connectString);
        return dataSource.getConnection();
    }

    public static Connection getPostgresConnection(String user,
                                                   String password,
                                                   String database,
                                                   String server,
                                                   int port) throws SQLException {
        return getPostgresDataSource(user,
                                     password == null ? "" : password,
                                     database,
                                     server,
                                     port,
                                     PGSimpleDataSource.class).getConnection();
    }

    public static DataSource getPostgresDataSource(String connectString) {
        return getPostgresDataSource(connectString, PGSimpleDataSource.class);
    }

    public static class PostgresConnectParameters {
        public String username;
        public String password;
        public String host;
        public int port;
        public String database;
    }

    public static DataSource getPostgresDataSource(String connectString, Class dataSourceClass) {
        PostgresConnectParameters postgresConnectParameters = extractPostgresConnectParameters(connectString);
        return Jdbc.getPostgresDataSource(postgresConnectParameters.username,
                                          postgresConnectParameters.password == null ? "" : postgresConnectParameters.password,
                                          postgresConnectParameters.database,
                                          postgresConnectParameters.host,
                                          postgresConnectParameters.port,
                                          dataSourceClass);
    }

    private static PostgresConnectParameters extractPostgresConnectParameters(String connectString) {
        PostgresConnectParameters postgresConnectParameters = new PostgresConnectParameters();
        Matcher noPasswordMatcher = Pattern.compile("^([^/@]+)@([^/]+)/([^/]+)$").matcher(connectString);
        if (noPasswordMatcher.matches()) {
            postgresConnectParameters.username = noPasswordMatcher.group(1);
            postgresConnectParameters.password = null;
            postgresConnectParameters.host = noPasswordMatcher.group(2);
            postgresConnectParameters.port = 5432;
            postgresConnectParameters.database = noPasswordMatcher.group(3);
        } else {
            Matcher withPasswordMatcher = Pattern.compile("^([^/@]+)/([^/@]+)@([^/:]+)/([^/]+)$").matcher(connectString);
            if (withPasswordMatcher.matches()) {
                postgresConnectParameters.username = withPasswordMatcher.group(1);
                postgresConnectParameters.password = withPasswordMatcher.group(2);
                postgresConnectParameters.host = withPasswordMatcher.group(3);
                postgresConnectParameters.port = 5432;
                postgresConnectParameters.database = withPasswordMatcher.group(4);
            } else {
                Matcher withPasswordAndPortMatcher = Pattern.compile("^([^/@]+)/([^/@]+)@([^/:]+):([^/:]+)/([^/]+)$").matcher(connectString);
                if (withPasswordAndPortMatcher.matches()) {
                    postgresConnectParameters.username = withPasswordAndPortMatcher.group(1);
                    postgresConnectParameters.password = withPasswordAndPortMatcher.group(2);
                    postgresConnectParameters.host = withPasswordAndPortMatcher.group(3);
                    postgresConnectParameters.port = Integer.parseInt(withPasswordAndPortMatcher.group(4));
                    postgresConnectParameters.database = withPasswordAndPortMatcher.group(5);
                } else {
                    throw new RuntimeException("Invalid PostgreSQL connect string \"" + connectString + "\"\n" +
                                               "(expected something of the form \"<user>/<password>@<server>[:port]/<database>\"");
                }
            }
        }
        return postgresConnectParameters;
    }

    public static Connection getConnectionFromHomeDirectoryConfigFile(String homeDirectoryConfigFile, String connectionName) {
        try {
            return getDataSourceFromHomeDirectoryConfigFile(homeDirectoryConfigFile, connectionName).getConnection();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static Connection getConnection(JdbcConnectionConfig connectionConfig) {
        try {

            if ("org.postgresql.ds.PGSimpleDataSource".equals(connectionConfig.getDataSourceOrDriverClassName())) {
                return getPostgresDataSource((String) connectionConfig.getConnectString()).getConnection();
            } else if ("net.sourceforge.jtds.jdbcx.JtdsDataSource".equals(connectionConfig.getDataSourceOrDriverClassName())) {
                return getSqlServerDataSource(connectionConfig.getConnectString()).getConnection();
            } else {
                throw new RuntimeException(String.format("%s is not currently a supported DataSource or JDBC Driver class\n" +
                                                         "(supported classes are: org.postgresql.ds.PGSimpleDataSource,\n" +
                                                         "                        placeholder.for.sqlserver)", connectionConfig.getDataSourceOrDriverClassName()));
            }

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static DataSource getDataSourceFromHomeDirectoryConfigFile(String homeDirectoryConfigFile, String connectionName) {
        ExtendedProperties configFileProperties = Io.getHomeDirectoryConfigFileProperties(homeDirectoryConfigFile);
        String datasourceClassName = configFileProperties.getMandatoryProperty(connectionName + ".connection.class");

        if ("org.postgresql.ds.PGSimpleDataSource".equals(datasourceClassName)) {
            return getPostgresDataSource((String) configFileProperties.getMandatoryProperty(connectionName + ".connection.connectstring"));
        } else if ("net.sourceforge.jtds.jdbcx.JtdsDataSource".equals(datasourceClassName)) {
            return getSqlServerDataSource(configFileProperties.getMandatoryProperty(connectionName + ".connection.connectstring"));
        } else {
            throw new RuntimeException(String.format("%s is not currently a supported DataSource class\n" +
                                                     "(supported classes are: org.postgresql.ds.PGSimpleDataSource,\n" +
                                                     "                        net.sourceforge.jtds.jdbcx.JtdsDataSource)", datasourceClassName));
        }

    }

    public static DataSource getDataSourceFrom(JdbcConnectionConfig jdbcConnectionConfig) {
        String datasourceClassName = jdbcConnectionConfig.getDataSourceOrDriverClassName();

        if ("org.postgresql.ds.PGSimpleDataSource".equals(datasourceClassName)) {
            return getPostgresDataSource((String) jdbcConnectionConfig.getConnectString());
        } else if ("net.sourceforge.jtds.jdbcx.JtdsDataSource".equals(datasourceClassName)) {
            return getSqlServerDataSource(jdbcConnectionConfig.getConnectString());
        } else {
            throw new RuntimeException(String.format("%s is not currently a supported DataSource class\n" +
                                                     "(supported classes are: org.postgresql.ds.PGSimpleDataSource,\n" +
                                                     "                        net.sourceforge.jtds.jdbcx.JtdsDataSource)", datasourceClassName));
        }

    }

    public static DataSource getDataSourceFrom(DataSourceConfig config) {
        String datasourceClassName = config.getDataSourceClass();

        try {
            if (Strings.isIn(datasourceClassName, new String[]{"org.postgresql.ds.PGSimpleDataSource",
                                                               "org.postgresql.ds.PGPoolingDataSource"})) {
                return getPostgresDataSource(config.getConnectString(), Class.forName(datasourceClassName));
            } else if ("net.sourceforge.jtds.jdbcx.JtdsDataSource".equals(datasourceClassName)) {
                return getSqlServerDataSource(config.getConnectString());
            } else {
                throw new RuntimeException(String.format("%s is not currently a supported DataSource class\n" +
                                                         "(supported classes are: org.postgresql.ds.PGPoolingDataSource,\n" +
                                                         "                        org.postgresql.ds.PGSimpleDataSource,\n" +
                                                         "                        net.sourceforge.jtds.jdbcx.JtdsDataSource)", datasourceClassName));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getDatabaseNameFrom(DataSourceConfig config) {
        String datasourceClassName = config.getDataSourceClass();
        if (Strings.isIn(datasourceClassName, new String[]{"org.postgresql.ds.PGSimpleDataSource",
                                                           "org.postgresql.ds.PGPoolingDataSource"})) {
            return extractPostgresConnectParameters(config.getConnectString()).database;
        } else {
            throw new RuntimeException(String.format("Cannot get database name from configuration for %s class\n" +
                                                     "(supported classes are: org.postgresql.ds.PGPoolingDataSource,\n" +
                                                     "                        org.postgresql.ds.PGSimpleDataSource)", datasourceClassName));
        }
    }

    public static String getUsernameFrom(DataSourceConfig config) {
        String datasourceClassName = config.getDataSourceClass();
        if (Strings.isIn(datasourceClassName, new String[]{"org.postgresql.ds.PGSimpleDataSource",
                                                           "org.postgresql.ds.PGPoolingDataSource"})) {
            return extractPostgresConnectParameters(config.getConnectString()).username;
        } else {
            throw new RuntimeException(String.format("Cannot get database name from configuration for %s class\n" +
                                                     "(supported classes are: org.postgresql.ds.PGPoolingDataSource,\n" +
                                                     "                        org.postgresql.ds.PGSimpleDataSource)", datasourceClassName));
        }
    }

    public static PostgresConnectParameters getConnectParametersFrom(DataSourceConfig config) {
        String datasourceClassName = config.getDataSourceClass();
        if (Strings.isIn(datasourceClassName, new String[]{"org.postgresql.ds.PGSimpleDataSource",
                                                           "org.postgresql.ds.PGPoolingDataSource"})) {
            return extractPostgresConnectParameters(config.getConnectString());
        } else {
            throw new RuntimeException(String.format("Cannot get database name from configuration for %s class\n" +
                                                     "(supported classes are: org.postgresql.ds.PGPoolingDataSource,\n" +
                                                     "                        org.postgresql.ds.PGSimpleDataSource)", datasourceClassName));
        }
    }

    public static DataSource getOracleDataSource(String connectString) {
        String userid;
        String password;
        String host;
        String sid = null;
        String serviceName = null;
        Matcher noPasswordMatcher = Pattern.compile("^([^/:@]+)@([^/:]+)([/:])([^/:]+)$").matcher(connectString);
        if (noPasswordMatcher.matches()) {
            userid = noPasswordMatcher.group(1);
            password = null;
            host = noPasswordMatcher.group(2);
            if (":".equals(noPasswordMatcher.group(3))) {
                sid = noPasswordMatcher.group(4);
            } else {
                serviceName = noPasswordMatcher.group(4);
            }
        } else {
            Matcher withPasswordMatcher = Pattern.compile("^([^/:@]+)/([^/:@]+)@([^/:]+)([/:])([^/:]+)$").matcher(connectString);
            if (withPasswordMatcher.matches()) {
                userid = withPasswordMatcher.group(1);
                password = withPasswordMatcher.group(2);
                host = withPasswordMatcher.group(3);
                if (":".equals(withPasswordMatcher.group(4))) {
                    sid = withPasswordMatcher.group(5);
                } else {
                    serviceName = withPasswordMatcher.group(5);
                }
            } else {
                throw new RuntimeException("Invalid PostgreSQL connect string \"" + connectString + "\"");
            }
        }
        if (sid != null) {
            return getOracleSIDDataSource(userid,
                                          password == null ? "" : password,
                                          host,
                                          sid,
                                          1521);
        } else {
            return getOracleServiceNameDataSource(userid,
                                                  password == null ? "" : password,
                                                  host,
                                                  serviceName,
                                                  1521);
        }
    }

    public static DataSource getSqlServerDataSource(String connectString) {
        String userid;
        String password;
        String host;
        int port = 1433;
        String database;
        Matcher noPasswordMatcher = Pattern.compile("^([^/@]+)@([^/]+)/([^/]+)$").matcher(connectString);
        if (noPasswordMatcher.matches()) {
            userid = noPasswordMatcher.group(1);
            password = null;
            host = noPasswordMatcher.group(2);
            database = noPasswordMatcher.group(3);
        } else {
            Matcher withPasswordMatcher = Pattern.compile("^([^/@]+)/([^/@]+)@([^/:]+)/([^/]+)$").matcher(connectString);
            if (withPasswordMatcher.matches()) {
                userid = withPasswordMatcher.group(1);
                password = withPasswordMatcher.group(2);
                host = withPasswordMatcher.group(3);
                database = withPasswordMatcher.group(4);
            } else {
                Matcher withPasswordAndPortMatcher = Pattern.compile("^([^/@]+)/([^/@]+)@([^/:]+):([0-9]+)/([^/]+)$").matcher(connectString);
                if (withPasswordAndPortMatcher.matches()) {
                    userid = withPasswordAndPortMatcher.group(1);
                    password = withPasswordAndPortMatcher.group(2);
                    host = withPasswordAndPortMatcher.group(3);
                    port = Integer.parseInt(withPasswordAndPortMatcher.group(4));
                    database = withPasswordAndPortMatcher.group(5);
                } else {
                    throw new RuntimeException("Invalid Sql Server connect string \"" + connectString + "\"\n" +
                                               "(expected something of the form \"<user>/<password>@<server>[:port]/<database>\"");
                }
            }
        }
        return getSqlServerDataSource(userid,
                                      password == null ? "" : password,
                                      database,
                                      host,
                                      port);
    }

    public static DataSource getSqlServerDataSource(String user,
                                                    String password,
                                                    String database,
                                                    String server,
                                                    int port) {

        // Setup the "base" data source
        final JtdsDataSource baseDataSource = new JtdsDataSource();
        baseDataSource.setUser(user);
        baseDataSource.setPassword(password);
        baseDataSource.setDatabaseName(database);
        baseDataSource.setServerName(server);
        baseDataSource.setPortNumber(port);
        return baseDataSource;
//        ConnectionFactory connectionFactory = new ConnectionFactory() {
//            public Connection createConnection() throws SQLException {
//                return baseDataSource.getConnection();
//            }
//        };
//
//        // Create and return a pooling data source based on the base data source
//        GenericObjectPool connectionPool = new GenericObjectPool(null);
//        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
//        return new PoolingDataSource(connectionPool);

    }

    public static DataSource getPostgresDataSource(String user,
                                                   String password,
                                                   String database,
                                                   String server,
                                                   int port,
                                                   Class dataSourceClass) {

//        org.postgresql.ds.PGPoolingDataSource pgDataSource = new org.postgresql.ds.PGPoolingDataSource();
        BaseDataSource pgDataSource = null;
        try {
            pgDataSource = (BaseDataSource) dataSourceClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        pgDataSource.setUser(user);
        pgDataSource.setPassword(password);
        pgDataSource.setDatabaseName(database);
        pgDataSource.setServerName(server);
        pgDataSource.setPortNumber(port);
        return (DataSource) pgDataSource;

    }

//    public static DataSource getTeraDataDataSource(String connectString) {
//        String user;
//        String password;
//        String server;
//        String database;
//        Matcher noPasswordMatcher = Pattern.compile("^([^/@]+)@([^/]+)/([^/]+)$").matcher(connectString);
//        if (noPasswordMatcher.matches()) {
//            user = noPasswordMatcher.group(1);
//            password = null;
//            server = noPasswordMatcher.group(2);
//            database = noPasswordMatcher.group(3);
//        } else {
//            Matcher withPasswordMatcher = Pattern.compile("^([^/@]+)/([^/@]+)@([^/]+)/([^/]+)$").matcher(connectString);
//            if (withPasswordMatcher.matches()) {
//                user = withPasswordMatcher.group(1);
//                password = withPasswordMatcher.group(2);
//                server = withPasswordMatcher.group(3);
//                database = withPasswordMatcher.group(4);
//            } else {
//                throw new RuntimeException("Invalid TeraData connect string \"" + connectString + "\"");
//            }
//        }
//        return getTeraDataDataSource(user,
//                                     password == null ? "" : password,
//                                     database,
//                                     server);
//    }

//    public static DataSource getTeraDataDataSource(String user,
//                                                   String password,
//                                                   String database,
//                                                   String server) {
//        TeraDataSource teraDataSource = new TeraDataSource();
//        teraDataSource.setuser(user);
//        teraDataSource.setpassword(password);
//        teraDataSource.setDatabaseName(database);
//        teraDataSource.setDSName(server);
//        return teraDataSource;
//
//    }

    public static DataSource getOracleSIDDataSource(String user,
                                                    String password,
                                                    String server,
                                                    String database,
                                                    int port) {
        OracleDataSource oracleDataSource;
        try {
            oracleDataSource = new OracleDataSource();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        oracleDataSource.setUser(user);
        oracleDataSource.setPassword(password);
        oracleDataSource.setServerName(server);
        oracleDataSource.setDatabaseName(database);
        oracleDataSource.setPortNumber(port);
        return oracleDataSource;

    }

    public static DataSource getOracleServiceNameDataSource(String user,
                                                            String password,
                                                            String server,
                                                            String serviceName,
                                                            int port) {
        OracleDataSource oracleDataSource;
        try {
            oracleDataSource = new OracleDataSource();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        oracleDataSource.setUser(user);
        oracleDataSource.setPassword(password);
        oracleDataSource.setDriverType("thin");
        oracleDataSource.setServerName(server);
        oracleDataSource.setServiceName(serviceName);
        oracleDataSource.setPortNumber(port);
        //oracleDataSource.setURL(String.format("jdbc:oracle:thin:@//%s:%d/%s", server, port, serviceName));

        return oracleDataSource;

    }

    public static Map mapRow(ResultSet rset) {
        try {
            int columnCount = rset.getMetaData().getColumnCount();
            Map columns = new TreeMap();
            for (int i = 1; i <= columnCount; i++) {
                columns.put(rset.getMetaData().getColumnName(i), rset.getObject(i));
            }
            return columns;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
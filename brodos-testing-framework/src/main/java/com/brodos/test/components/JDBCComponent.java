/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import com.brodos.test.TestNGRunner;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
public class JDBCComponent {

    private static final Map<String, JDBCComponent> JDBCCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "jdbc.properties";
    private static final Integer DEFAULT_POOL_SIZE = 10;

    private final String configFileName;
    private final Properties properties = new Properties();
    private BasicDataSource basicDataSource = null;

    private JDBCComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private JDBCComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static JDBCComponent instance() throws Exception {
        if (!JDBCCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            JDBCCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new JDBCComponent());
        }

        return JDBCCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static JDBCComponent instance(String configFileName) throws Exception {
        if (!JDBCCOMPONENT_MAP.containsKey(configFileName)) {
            JDBCCOMPONENT_MAP.put(configFileName, new JDBCComponent(configFileName));
        }

        return JDBCCOMPONENT_MAP.get(configFileName);
    }

    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    synchronized public Connection getConnection() throws Exception {
        if (basicDataSource == null || basicDataSource.isClosed()) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(properties.getProperty("driver"));
            basicDataSource.setUrl(properties.getProperty("datasource.url"));
            basicDataSource.setUsername(properties.getProperty("username"));
            basicDataSource.setPassword(properties.getProperty("password"));
            String poolsize = properties.getProperty("poolsize");
            if (!StringUtils.isBlank(poolsize) && StringUtils.isNumeric(poolsize)) {
                basicDataSource.setInitialSize(Integer.valueOf(poolsize));
                basicDataSource.setMaxIdle(Integer.valueOf(poolsize));
                basicDataSource.setMaxTotal(Integer.valueOf(poolsize));
            } else {
                basicDataSource.setInitialSize(DEFAULT_POOL_SIZE);
                basicDataSource.setMaxIdle(DEFAULT_POOL_SIZE);
                basicDataSource.setMaxTotal(DEFAULT_POOL_SIZE);
            }
        }

        return basicDataSource.getConnection();
    }

    public List<JSONObject> executeQuery(String sql) throws Exception {
        if (TestNGRunner.instance().isDebugEnabled()) {
            System.out.println("sql: " + sql);
        }
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement();) {
            return resultSetToArrayList(statement.executeQuery(sql));
        }
    }

    public List<JSONObject> executeQuery(String sql, long maxRetryCount, long retryInterval) throws Exception {
        if (TestNGRunner.instance().isDebugEnabled()) {
            System.out.println("sql: " + sql);
        }

        List<JSONObject> result = executeQuery(sql);
        int count = 0;
        while (result.isEmpty() && count < maxRetryCount) {
            result = executeQuery(sql);
            Thread.sleep(retryInterval);
            count++;
        }

        return result;
    }

    public List<JSONObject> resultSetToArrayList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<JSONObject> list = new ArrayList();
        while (rs.next()) {
            JSONObject row = new JSONObject();
            for (int i = 1; i <= columns; ++i) {
                row.put(String.valueOf(i), rs.getObject(i));
            }

            list.add(row);
        }

        rs.close();
        return list;
    }
}

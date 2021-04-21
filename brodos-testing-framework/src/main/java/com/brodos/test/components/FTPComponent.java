/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author padhaval
 */
public class FTPComponent {

    private static final Map<String, FTPComponent> FTPCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "ftp.properties";
    
    private final String configFileName;
    private final Properties properties = new Properties();
    
    private FTPClient fTPClient;
    
    private FTPComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private FTPComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static FTPComponent instance() throws Exception {
        if (!FTPCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            FTPCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new FTPComponent());
        }

        return FTPCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static FTPComponent instance(String configFileName) throws Exception {
        if (!FTPCOMPONENT_MAP.containsKey(configFileName)) {
            FTPCOMPONENT_MAP.put(configFileName, new FTPComponent(configFileName));
        }

        return FTPCOMPONENT_MAP.get(configFileName);
    }
    
    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    public FTPClient getFTPClient() throws Exception {
        if (fTPClient == null
                || !fTPClient.isConnected()
                || !fTPClient.isAvailable()) {
            fTPClient = new FTPClient();
            fTPClient.connect(properties.getProperty("host"), Integer.valueOf(properties.getProperty("port")));
            fTPClient.login(properties.getProperty("username"), properties.getProperty("password"));
        }

        return fTPClient;
    }
}

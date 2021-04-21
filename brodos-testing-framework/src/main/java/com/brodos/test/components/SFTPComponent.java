/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author padhaval
 */
public class SFTPComponent {

    private static final Map<String, SFTPComponent> SFTPCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "sftp.properties";

    private final String configFileName;
    private final Properties properties = new Properties();

    private Session session;

    private SFTPComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private SFTPComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static SFTPComponent instance() throws Exception {
        if (!SFTPCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            SFTPCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new SFTPComponent());
        }

        return SFTPCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static SFTPComponent instance(String configFileName) throws Exception {
        if (!SFTPCOMPONENT_MAP.containsKey(configFileName)) {
            SFTPCOMPONENT_MAP.put(configFileName, new SFTPComponent(configFileName));
        }

        return SFTPCOMPONENT_MAP.get(configFileName);
    }

    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    public Session getJSchSession() throws Exception {
        if (session == null
                || !session.isConnected()) {
            JSch jSch = new JSch();
            session = jSch.getSession(properties.getProperty("username"), properties.getProperty("host"), Integer.valueOf(properties.getProperty("port")));
            if (StringUtils.isBlank(properties.getProperty("password"))) {
                session.setPassword(properties.getProperty("password"));
            } else {
                jSch.addIdentity(properties.getProperty("prvkey"));
            }

            session.connect();
        }

        return session;
    }
}

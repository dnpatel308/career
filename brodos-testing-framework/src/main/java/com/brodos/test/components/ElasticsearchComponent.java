/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import com.brodos.test.Utils;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 *
 * @author padhaval
 */
public class ElasticsearchComponent {

    private static final Map<String, ElasticsearchComponent> ELASTICSEARCHCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "elasticsearch.properties";

    private final String configFileName;
    private final Properties properties = new Properties();

    private RestHighLevelClient restHighLevelClient;

    private ElasticsearchComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private ElasticsearchComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static ElasticsearchComponent instance() throws Exception {
        if (!ELASTICSEARCHCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            ELASTICSEARCHCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new ElasticsearchComponent());
        }

        return ELASTICSEARCHCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static ElasticsearchComponent instance(String configFileName) throws Exception {
        if (!ELASTICSEARCHCOMPONENT_MAP.containsKey(configFileName)) {
            ELASTICSEARCHCOMPONENT_MAP.put(configFileName, new ElasticsearchComponent(configFileName));
        }

        return ELASTICSEARCHCOMPONENT_MAP.get(configFileName);
    }

    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    private boolean isConnected() {
        try {
            return restHighLevelClient.ping(RequestOptions.DEFAULT);
        } catch (Exception ex) {
            Utils.printException(ex);
            return false;
        }
    }

    public RestHighLevelClient getRestHighLevelClient() {
        if (restHighLevelClient == null
                || isConnected()) {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(properties.getProperty("hostname"), Integer.valueOf(properties.getProperty("port")), properties.getProperty("scheme"))));
        }

        return restHighLevelClient;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author padhaval
 */
public class AmazonS3ClientComponent {

    private static final Map<String, AmazonS3ClientComponent> AMAZONS3CLIENTCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "awss3.properties";

    private final String configFileName;
    private final Properties properties = new Properties();

    private AmazonS3 amazonS3;

    private AmazonS3ClientComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private AmazonS3ClientComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static AmazonS3ClientComponent instance() throws Exception {
        if (!AMAZONS3CLIENTCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            AMAZONS3CLIENTCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new AmazonS3ClientComponent());
        }

        return AMAZONS3CLIENTCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static AmazonS3ClientComponent instance(String configFileName) throws Exception {
        if (!AMAZONS3CLIENTCOMPONENT_MAP.containsKey(configFileName)) {
            AMAZONS3CLIENTCOMPONENT_MAP.put(configFileName, new AmazonS3ClientComponent(configFileName));
        }

        return AMAZONS3CLIENTCOMPONENT_MAP.get(configFileName);
    }

    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    public AmazonS3 getAmazonS3() {
        if (amazonS3 == null) {
            amazonS3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getProperty("service-endpoint"), properties.getProperty("region")))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(properties.getProperty("access-key-id"), properties.getProperty("secret-access-key"))))
                    .withPathStyleAccessEnabled(true)
                    .build();
        }

        return amazonS3;
    }
}

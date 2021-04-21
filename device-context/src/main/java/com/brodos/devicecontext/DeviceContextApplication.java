package com.brodos.devicecontext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeviceContextApplication {

    public static final String CONTEXT_PATH = "/erp-context/v1/device-context";

    public static void main(String[] args) {
        SpringApplication.run(DeviceContextApplication.class, args);
    }

}

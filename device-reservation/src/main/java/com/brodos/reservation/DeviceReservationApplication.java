package com.brodos.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeviceReservationApplication {

    public static final String CONTEXT_PATH = "/erp-context/v1/device-reservation/";

    public static void main(String[] args) {
        SpringApplication.run(DeviceReservationApplication.class, args);
    }
}

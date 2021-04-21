/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class AsyncComponent implements AsyncConfigurer {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${async.pool.size}")
    private Integer poolSize;

    @Value("${async.pool.max.size}")
    private Integer poolMaxSize;

    @PostConstruct
    public void postConstruct() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolMaxSize);
        threadPoolTaskExecutor.initialize();
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return threadPoolTaskExecutor;
    }
}

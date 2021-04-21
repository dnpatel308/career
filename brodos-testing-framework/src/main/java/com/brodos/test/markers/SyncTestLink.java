/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.markers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author padhaval
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SyncTestLink {

    public String fullTestCaseExternalId();

    public int testStepNumber();

    public int version() default 1;

    public String projectName() default "";

    public String testPlanName() default "";

    public String testBuildName() default "";
}

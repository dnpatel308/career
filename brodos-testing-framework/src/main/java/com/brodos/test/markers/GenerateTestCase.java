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
@Target(ElementType.METHOD)
public @interface GenerateTestCase {

    public String configFileName() default "";

    public boolean includeOnlyInParallel() default false;

    public int order() default 0;
    
    public boolean isCustomized() default false;
    
    public String headers() default "{}";
}

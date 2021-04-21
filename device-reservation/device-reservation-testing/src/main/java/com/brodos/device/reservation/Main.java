/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation;

import com.brodos.test.TestNGRunner;

/**
 *
 * @author padhaval
 */
public class Main {

    public static void main(String[] args) throws Exception {                   
        TestNGRunner.instance().setCallerClass(Main.class).initEnvMapAndCommonHeadersMap(args).run();     
        System.exit(0);
    }    
}

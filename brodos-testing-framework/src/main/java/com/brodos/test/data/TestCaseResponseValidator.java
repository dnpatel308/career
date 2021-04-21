/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data;

import io.restassured.response.Response;

/**
 *
 * @author padhaval
 */
public abstract class TestCaseResponseValidator {

    public abstract void validateResponse(Response response);
}

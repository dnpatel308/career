/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.article.domain.service;

import com.brodos.alg.domain.entity.Countrykeys;

/**
 *
 * @author padhaval
 */
public interface CountrykeysRepository {

    Countrykeys findCountrykeyByIsocodeAlpha2(String isocodeAlpha2);
    
    Countrykeys findCountrykeyByIsocodeAlpha3(String isocodeAlpha3);
}

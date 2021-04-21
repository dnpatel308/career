/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.article.domain.persistence;

import java.util.List;

import com.brodos.alg.domain.entity.TofRouteFigureKey;

/**
 *
 * @author padhaval
 */
public interface TofRouteFigureKeyRepository {

    TofRouteFigureKey findByCountryCallingCodeAndPostalRange(String countryCallingCode, String postalCode);

    int deleteAll();

    List<TofRouteFigureKey> saveAll(List<TofRouteFigureKey> tofRouteFigureKeys);
}

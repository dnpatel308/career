/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.article.domain.persistence;

import com.brodos.alg.domain.entity.DhlTimerangeCodes;



/**
 *
 * @author padhaval
 */
public interface DHLTimerangeCodesRepository {

    public DhlTimerangeCodes findByPk(String key);
}

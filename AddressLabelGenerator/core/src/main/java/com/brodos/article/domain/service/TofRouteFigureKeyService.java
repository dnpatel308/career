package com.brodos.article.domain.service;

import java.util.List;

import com.brodos.alg.domain.entity.TofRouteFigureKey;

public interface TofRouteFigureKeyService {
    public List<TofRouteFigureKey> removeAndCreateRouteFigureKey(List<TofRouteFigureKey> tofRouteFigureKey);
}

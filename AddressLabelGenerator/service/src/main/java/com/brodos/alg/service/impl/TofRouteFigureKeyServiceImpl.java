package com.brodos.alg.service.impl;

import java.util.List;

import javax.inject.Singleton;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.domain.entity.TofRouteFigureKey;
import com.brodos.article.domain.service.DomainRegistryService;
import com.brodos.article.domain.service.TofRouteFigureKeyService;

@OsgiServiceProvider(classes = { TofRouteFigureKeyService.class })
@Singleton
public class TofRouteFigureKeyServiceImpl implements TofRouteFigureKeyService {
    private static final Logger LOG = LoggerFactory.getLogger(TofRouteFigureKeyServiceImpl.class);

    @Override
    public List<TofRouteFigureKey> removeAndCreateRouteFigureKey(List<TofRouteFigureKey> tofRouteFigureKey) {
        int removeRecords = DomainRegistryService.instance().tofRouteFigureKeyRepository().deleteAll();
        LOG.info("Deleted All TofRouteFigureKey, size={}", removeRecords);
        return DomainRegistryService.instance().tofRouteFigureKeyRepository().saveAll(tofRouteFigureKey);
    }
}

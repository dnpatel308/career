package com.brodos.alg.application.boundary.impl;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.brodos.alg.application.assembler.TOFAssembler;
import com.brodos.alg.application.boundary.TOFOperationResource;
import com.brodos.alg.application.dto.TofRouteFigureKeyDTO;
import com.brodos.alg.domain.entity.TofRouteFigureKey;
import com.brodos.article.domain.service.DomainRegistryService;

@Singleton
@Named("tofOperationResource")
public class TOFOperationResourceImpl implements TOFOperationResource {

    @Override
    public TofRouteFigureKeyDTO uploadTofRouteFigureKeys(MultipartBody multipartBody) {
        List<TofRouteFigureKey> tofRouteFigureKeys = TOFAssembler.toTofRouteFigureKey(multipartBody);
        return TOFAssembler.fromRouteFigureKey(DomainRegistryService.instance().tofRouteFigureKeyService()
            .removeAndCreateRouteFigureKey(tofRouteFigureKeys), tofRouteFigureKeys.size());
    }
}

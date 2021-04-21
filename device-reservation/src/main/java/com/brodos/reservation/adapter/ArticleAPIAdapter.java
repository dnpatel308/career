/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.adapter;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.dto.response.ArticleDTO;
import com.brodos.reservation.exception.DeviceReservationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class ArticleAPIAdapter {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ArticleAPIAdapter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${articleapi.url}")
    private String articleAPIUrl;

    public List<ArticleDTO> callArticleAPI(String ean, String articleNumber, Long tenant, String languageKey) {
        try {
            GetRequest getRequest = Unirest.get(articleAPIUrl);

            getRequest.queryString("tenant", tenant);

            LOG.debug("articleAPIUrl={}, ean={}, articleNo={}, languagekey={}", articleAPIUrl, ean, articleNumber,
                languageKey);

            if (!StringUtils.isEmpty(ean)) {
                getRequest.queryString("ean", ean);
            } else if (!StringUtils.isEmpty(articleNumber)) {
                getRequest.queryString("articlenumber", articleNumber);
            }

            if (StringUtils.isEmpty(languageKey)) {
                getRequest.queryString("language", "DEU");
            } else {
                getRequest.queryString("language", languageKey);
            }

            ArticleDTO[] articleDTOs = null;

            JSONObject jsono = getRequest.asJson().getBody().getObject();
            if (jsono.has("data") && !jsono.isNull("data")) {
                String data = jsono.get("data").toString();
                articleDTOs = objectMapper.readValue(data, ArticleDTO[].class);
                return Arrays.asList(articleDTOs);
            } else {
                return Collections.emptyList();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.ARTICLE_API_ERROR);
        }
    }
}

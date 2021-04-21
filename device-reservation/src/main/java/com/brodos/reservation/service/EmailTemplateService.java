package com.brodos.reservation.service;

import java.util.Map;

public interface EmailTemplateService {

    public String getFormattedContent(String tempateName, Map<String, String> paramMap);
}

package com.brodos.reservation.service.impl;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.service.EmailTemplateService;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailtemplateServiceImpl implements EmailTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailtemplateServiceImpl.class);

    @Value("${email.template.directory}")
    private String templateDirectory;

    @Override
    public String getFormattedContent(String templateName, Map<String, String> paramMap) {

        try {
            Configuration cfg = new Configuration();
            cfg.setDefaultEncoding("UTF-8");
            cfg.setOutputEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template template = cfg.getTemplate(templateDirectory + templateName, "UTF-8");
            Writer out = new StringWriter();
            template.process(paramMap, out);
            return out.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new DeviceReservationException(ErrorCodes.TEMPLATE_EXCEPTION);
        }
    }
}

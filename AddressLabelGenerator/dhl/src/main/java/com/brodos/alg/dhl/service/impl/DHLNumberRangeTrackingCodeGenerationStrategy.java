package com.brodos.alg.dhl.service.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import org.json.JSONObject;

import com.brodos.alg.dhl.service.DHLTrackingCodeGenerationStrategy;
import com.brodos.alg.domain.entity.IntegerSequenceFormatter;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.article.domain.service.DomainRegistryService;

@Singleton
@Named("dhlNumberRangeTrackingCodeGenerationStrategy")
public class DHLNumberRangeTrackingCodeGenerationStrategy implements DHLTrackingCodeGenerationStrategy {

    @Override
    synchronized public String getTrackingCode(JSONObject trackingCodeData) {
        String client = trackingCodeData.getString("client");
        IntegerSequenceFormatter sequenceFormatter = DomainRegistryService.instance().sequenceFormatterRepository()
            .getIncrementalFormatterForType(trackingCodeData.getString("client"));
        if (sequenceFormatter == null) {
            throw new ALGException(10030,
                "Field 'customerNumber' contains an invalid entry. Please provide a valid entry");
        }
        if (client.equalsIgnoreCase("TELEKOM1")) {
            return appendCheckDigitTelekom(sequenceFormatter.getFormatedIncrementalValue());
        }
        return appendCheckDigit(sequenceFormatter.getFormatedIncrementalValue());
    }

    private String appendCheckDigit(String trackingCode) {
        String onlyNumericTrackingCode = trackingCode.replaceAll("[^\\d.+]", "").replace(".", "").replace(" ", "");
        int sum = 0;
        for (int i = 3; i <= onlyNumericTrackingCode.length(); i++) {
            if (i % 2 != 0) {
                sum += Character.getNumericValue(onlyNumericTrackingCode.charAt(i - 1)) * 3;
            } else {
                sum += Character.getNumericValue(onlyNumericTrackingCode.charAt(i - 1)) * 1;
            }
        }

        Integer checkDigit = 0;
        if (sum % 10 != 0) {
            int nextMultipleOfTen = ((sum / 10) + 1) * 10;
            checkDigit = (nextMultipleOfTen - sum);
        }

        return onlyNumericTrackingCode + checkDigit.toString();
    }

    private String appendCheckDigitTelekom(String trackingCode) {
        String onlyNumericTrackingCode = trackingCode.replaceAll("[^\\d.+]", "").replace(".", "").replace(" ", "");
        int sum = 0;
        for (int i = 1; i <= onlyNumericTrackingCode.length(); i++) {
            if (i % 2 != 0) {
                sum += Character.getNumericValue(onlyNumericTrackingCode.charAt(i - 1)) * 4;
            } else {
                sum += Character.getNumericValue(onlyNumericTrackingCode.charAt(i - 1)) * 9;
            }
        }

        Integer checkDigit = 0;
        if (sum % 10 != 0) {
            int nextMultipleOfTen = ((sum / 10) + 1) * 10;
            checkDigit = (nextMultipleOfTen - sum);
        }

        return onlyNumericTrackingCode + checkDigit.toString();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.tof.util;

import com.brodos.alg.domain.entity.ALGSequence;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.entity.IntegerSequenceFormatter;
import com.brodos.alg.domain.entity.TofCustomerNumbers;
import com.brodos.alg.domain.entity.TofSevicecodes;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.alg.domain.util.Utils;
import com.brodos.article.domain.service.DomainRegistryService;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class TOFUtils {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(TOFUtils.class);
    
    private static final String DATE_FORMAT = "yyMMdd";
    private static final String TIME_FORMAT = "HHmm";

    synchronized public static String getFormattedWeigthString(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        String pkgWeight = "";
        Integer weightInIntegerRepresentation = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "weight.weightInIntegerRepresentation", stringKeys, integerKeys, integerKeys);
        String unit = Utils.getStringValueOfValidatedJSONKey(requestJson, "weight.unit", stringKeys, integerKeys, integerKeys);

        switch (unit.toLowerCase()) {
            case "gramms":
            case "g": {
                float weightInKg = (float) weightInIntegerRepresentation / 1000f;
                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setMaximumFractionDigits(2);
                pkgWeight = String.valueOf((int) (Float.valueOf(decimalFormat.format(weightInKg)) * 100));
                pkgWeight = StringUtils.leftPad(pkgWeight, 7, "0");
                break;
            }

            default: {
                throw new ALGException(10008, "Field 'weight unit' contains an invalid entry. Please provide a valid entry");
            }
        }

        return pkgWeight;
    }

    synchronized public static Integer getValidatedCollectionType(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        Integer collectionType = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.collectionType", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (collectionType != 3 && collectionType != 5 && !excludeFieldsFromValidation.contains("freightForwarder.customProperties.collectionType")) {
            throw new ALGException(10014, "Field 'collection type' contains an invalid entry. Please provide a valid entry");
        }

        return collectionType;
    }

    synchronized public static Integer getValidatedTermsOfDeliveryCode(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        Integer termsOfDeliveryCode = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.termsOfDeliveryCode", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (termsOfDeliveryCode < 0 || termsOfDeliveryCode > 5 && !excludeFieldsFromValidation.contains("freightForwarder.customProperties.termsOfDeliveryCode")) {
            throw new ALGException(10015, "Field 'terms of delivery code' contains an invalid entry. Please provide a valid entry");
        }

        return termsOfDeliveryCode;
    }

    synchronized public static String getValidatedShippingUnitCode(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        String shippingUnitCode = Utils.getStringValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.shippingUnitCode", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (!shippingUnitCode.equals("P") && !shippingUnitCode.equals("C") && !excludeFieldsFromValidation.contains("freightForwarder.customProperties.shippingUnitCode")) {
            throw new ALGException(10016, "Field 'shipping unit code' contains an invalid entry. Please provide a valid entry");
        }

        return shippingUnitCode;
    }

    synchronized public static String generateBarcodeString(JSONObject requestJson, String countryCode, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {

        LOG.debug("requestJson={}", requestJson);
        String postalCode = Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.postalCode", stringKeys, integerKeys, excludeFieldsFromValidation);
        Integer packageNoOutOfTotalPackages = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "packageNoOutOfTotalPackages", stringKeys, integerKeys, excludeFieldsFromValidation);
        String parcelNumber = StringUtils.leftPad(packageNoOutOfTotalPackages.toString(), 3, "0");
        
        LOG.debug("parcelNumber={}", parcelNumber);
        if (parcelNumber.length() > 3) {
            throw new ALGException(10026, "Field 'package number out of total packages' contains an invalid entry. Please provide a valid entry");
        }
        
        IntegerSequenceFormatter sequenceFormatter = DomainRegistryService.instance().sequenceFormatterRepository().getIncrementalFormatterForType(ALGSequence.TOF);
        parcelNumber = sequenceFormatter.getFormatedIncrementalValue() + parcelNumber;

        String customerNumber = Utils.getStringValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.customerNumber", stringKeys, integerKeys, excludeFieldsFromValidation);
        TofCustomerNumbers tofCustomerNumbers = DomainRegistryService.instance().tofCustomerNumbersRepository().findCustomerNumberByCustomerNumber(Integer.parseInt(customerNumber));        

        if (tofCustomerNumbers == null) {
            throw new ALGException(10020, "Field 'customer number' contains an invalid entry. Please provide a valid entry");
        }

        String barcodeNumber = tofCustomerNumbers.getBarcodeNumber();
        return String.format("%s-%s-%s-%s-%s", barcodeNumber, parcelNumber.subSequence(0, 6), parcelNumber.subSequence(6, 12), countryCode, postalCode);
    }

    synchronized public static String generateQRCodeString(AddressLabel addressLabel, Integer qrcodeVersion, String vehicleRegistrationCode, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        //        "Header|Version|Input data|Customer number|Parcel reference (barcode information)|Shipment reference|Special service|Code for shipping unit|Number of packages|Number of complete pallets|Package weight|Recipient name 1|Recipient name 2|Street|House number|Country code|Postal code|Town|Recipient phone number|Recipient Email address|Terms of delivery code1|Freight amount|Freight currency|COD amount|COD currency|Collection type|Delivery data|Delivery time|Volume|trans-o-flex internal use|Alternative ref. number 1|trans-o-flex internal use|Alternative ref. number 2|trans-o-flex internal use|Alternative ref. number 3|Supply Chain information"
        JSONObject requestJson = addressLabel.getRequestJson();
        String qrcodeFormat = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s";

        if (qrcodeVersion == 12) {
            qrcodeFormat = qrcodeFormat.replace("|", "*");
        }

        String pkgWeight = TOFUtils.getFormattedWeigthString(requestJson, stringKeys, integerKeys, excludeFieldsFromValidation);
        Integer collectionType = TOFUtils.getValidatedCollectionType(requestJson, stringKeys, integerKeys, excludeFieldsFromValidation);
        Integer termsOfDeliveryCode = TOFUtils.getValidatedTermsOfDeliveryCode(requestJson, stringKeys, integerKeys, excludeFieldsFromValidation);
        String shippingUnitCode = TOFUtils.getValidatedShippingUnitCode(requestJson, stringKeys, integerKeys, excludeFieldsFromValidation);
        String customerNumber = Utils.getStringValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.customerNumber", stringKeys, integerKeys, excludeFieldsFromValidation);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat stf = new SimpleDateFormat(TIME_FORMAT);

        String deliveryDate = "";
        String deliveryTime = "";

        Integer specialServiceCode = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.specialServiceCode", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (specialServiceCode == 10) {
            Date deliveryTimestamp = addressLabel.getDeliveryTimestamp();
            if (deliveryTimestamp == null && !excludeFieldsFromValidation.contains("deliveryTimestamp")) {
                throw new ALGException(10007, "Field 'delivery timestamp' contains an invalid entry. Please provide a valid entry");
            } else {
                deliveryDate = sdf.format(deliveryTimestamp);
                deliveryTime = stf.format(deliveryTimestamp);
            }
        }

        String shippingType = TOFUtils.getValidatedShippingType(addressLabel.getRequestJson(), stringKeys, integerKeys, excludeFieldsFromValidation);
        if (shippingType.equalsIgnoreCase("national")) {
            vehicleRegistrationCode = "DE";
        }

        String specialServiceCodeString = "";
        if (specialServiceCode != 0) {
            specialServiceCodeString = StringUtils.leftPad(specialServiceCode.toString(), 2, "0");
        }

        String qrcode = String.format(qrcodeFormat,
                "TOF",
                qrcodeVersion,
                sdf.format(new Date()),
                customerNumber,
                addressLabel.getRequestJson().getString("barcode").replace("-", ""),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.shipmentReferenceNumber", stringKeys, integerKeys, excludeFieldsFromValidation),
                specialServiceCodeString,
                shippingUnitCode,
                Utils.getIntegerValueOfValidatedJSONKey(requestJson, "totalNoOfPackages", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.noOfCompletePalletes", stringKeys, integerKeys, excludeFieldsFromValidation),
                pkgWeight,
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.name1", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.name2", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.street", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.houseNo", stringKeys, integerKeys, excludeFieldsFromValidation),
                vehicleRegistrationCode,
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.postalCode", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.city", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.phoneNo", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "recipient.email", stringKeys, integerKeys, excludeFieldsFromValidation),
                termsOfDeliveryCode,
                Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freight.amountInLowestDenomination", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "freight.currency", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getIntegerValueOfValidatedJSONKey(requestJson, "cod.amount", stringKeys, integerKeys, excludeFieldsFromValidation),
                Utils.getStringValueOfValidatedJSONKey(requestJson, "cod.currency", stringKeys, integerKeys, excludeFieldsFromValidation),
                collectionType,
                deliveryDate, // Delivery date
                deliveryTime, // Delivery time
                "", // Volume
                "", // trans-o-flex internal use
                "", // Alternative ref. number 1
                "", // trans-o-flex internal use
                "", // Alternative ref. number 2
                "", // trans-o-flex internal use
                "", // Alternative ref. number 3
                "" // Supply Chain information
        );

        qrcode = StringUtils.rightPad(qrcode, 362, " ");
        return qrcode;
    }

    synchronized public static String getValidatedShippingType(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        String shipmentType = Utils.getStringValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.shipmentType", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (!shipmentType.equalsIgnoreCase("national") && !shipmentType.equalsIgnoreCase("international")) {
            throw new ALGException(10009, "Field 'shipment type' contains an invalid entry. Please provide a valid entry");
        }

        return shipmentType;
    }

    synchronized public static String getValidatedSpecialServices(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        String specialServices = "";

        Integer specialServiceCode = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.specialServiceCode", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (specialServiceCode != 0) {
            TofSevicecodes sevicecodes = DomainRegistryService.instance().tofSevicecodesRepository().findSevicecodeBySevicecode(specialServiceCode);
            if (sevicecodes != null) {
                specialServices = sevicecodes.getServiceName();
            } else {
                throw new ALGException(10023, "Field 'special service code' contains an invalid entry. Please provide a valid entry");
            }
        }

        Integer codAmount = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "cod.amount", stringKeys, integerKeys, excludeFieldsFromValidation);
        String codCurrency = Utils.getStringValueOfValidatedJSONKey(requestJson, "cod.currency", stringKeys, integerKeys, excludeFieldsFromValidation);

        if (!StringUtils.isBlank(codCurrency) && codAmount > 0) {
            if (!StringUtils.isBlank(specialServices)) {
                specialServices += "|";
            }

            specialServices += "NN";
        }

        return specialServices;
    }

    synchronized public static Integer getValidatedSpecialServiceCode(JSONObject requestJson, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        Integer specialServiceCode = Utils.getIntegerValueOfValidatedJSONKey(requestJson, "freightForwarder.customProperties.specialServiceCode", stringKeys, integerKeys, excludeFieldsFromValidation);

        if (specialServiceCode != 0) {
            TofSevicecodes sevicecodes = DomainRegistryService.instance().tofSevicecodesRepository().findSevicecodeBySevicecode(specialServiceCode);
            if (sevicecodes == null) {
                throw new ALGException(10023, "Field 'special service code' contains an invalid entry. Please provide a valid entry");
            }

            return sevicecodes.getServiceCode();
        } else {
            return specialServiceCode;
        }
    }
}

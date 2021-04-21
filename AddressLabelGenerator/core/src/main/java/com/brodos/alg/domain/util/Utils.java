/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.util;

import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.exception.ALGException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class Utils {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Utils.class);
    
    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>();
        list1.addAll(Arrays.asList("freightForwarder.customProperties.collectionType|freightForwarder.customProperties.termsOfDeliveryCode|freightForwarder.customProperties.specialServiceCode|freightForwarder.customProperties.noOfCompletePalletes|freight.amountInLowestDenomination|cod.amount|freightForwarder.customProperties.specialServiceCode|cod.amount".split("\\|")));
        Set<String> keys = new TreeSet<>();
        keys.addAll(list1);

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keys) {            
            if (stringBuilder.length() > 0) {
                stringBuilder.append("|");
            }

            stringBuilder.append(key);
        }

        System.out.println(stringBuilder);
    }

    synchronized public static String getFieldName(String path) {
        String fieldName;
        if (path != null && path.contains(".")) {
            fieldName = StringUtils.substringAfterLast(path, ".");
        } else {
            fieldName = path;
        }

        if (!StringUtils.isBlank(fieldName)) {
            fieldName = fieldName.replace("No", "Number");
            fieldName = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(fieldName), StringUtils.SPACE);
            fieldName = fieldName.toLowerCase();
        }

        return fieldName;
    }

    synchronized public static <T> T getValueOfValidatedJSONKey(JSONObject jsono, String path, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        T returnValue = null;

        if (stringKeys.contains(path)) {
            returnValue = (T) "";
        } else if (integerKeys.contains(path)) {
            returnValue = (T) (Integer) 0;
        }

        String[] jsonKeys = null;
        if (path.contains(".")) {
            jsonKeys = path.split("\\.");
        } else {
            jsonKeys = new String[1];
            jsonKeys[0] = path;
        }

        JSONObject extractedJSONObject = jsono;
        for (String jsonKey : jsonKeys) {
            if (extractedJSONObject != null && extractedJSONObject.has(jsonKey)) {
                Object object = extractedJSONObject.get(jsonKey);
                if (object instanceof JSONObject) {
                    extractedJSONObject = (JSONObject) object;
                } else {
                    if (stringKeys.contains(path)) {
                        if (object instanceof String) {
                            if (!excludeFieldsFromValidation.contains(path) && StringUtils.isBlank(object.toString())) {
                                throw new ALGException(10010, "Field '" + getFieldName(path) + "' is blank. Please provide a valid " + getFieldName(path));
                            }
                        } else {
                            throw new ALGException(10011, "Field '" + getFieldName(path) + "' contains an invalid entry. Please provide a valid entry");
                        }

                        if (object == null) {
                            object = "";
                        }

                        returnValue = (T) object.toString();
                    } else if (integerKeys.contains(path)) {
                        if (!(object instanceof Integer)) {
                            throw new ALGException(10011, "Field '" + getFieldName(path) + "' contains an invalid entry. Please provide a valid entry");
                        }

                        if (object == null) {
                            object = 0;
                        }

                        returnValue = (T) (Integer) object;
                    }
                }
            } else {
                if (!excludeFieldsFromValidation.contains(path)) {
                    throw new ALGException(10010, "Field '" + getFieldName(path) + "' is blank. Please provide a valid " + getFieldName(path));
                }
            }
        }

        return returnValue;
    }

    synchronized public static String getStringValueOfValidatedJSONKey(JSONObject jsono, String path, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation
    ) {
        return getValueOfValidatedJSONKey(jsono, path, stringKeys, integerKeys, excludeFieldsFromValidation);
    }

    synchronized public static Integer getIntegerValueOfValidatedJSONKey(JSONObject jsono, String path, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation
    ) {
        return getValueOfValidatedJSONKey(jsono, path, stringKeys, integerKeys, excludeFieldsFromValidation);
    }

    private static String createPath(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                if (i > 0) {
                    stringBuilder.append(".");
                }

                String key = keys.get(i);
                stringBuilder.append(key);
            }
            return stringBuilder.toString();
        }

        return "";
    }

    synchronized public static JSONObject createFlatJSONObject(JSONObject srcJSONObject, JSONObject destJSONObject, List<String> keys) {
        if (destJSONObject == null) {
            destJSONObject = new JSONObject();
        }
        if (srcJSONObject != null && !srcJSONObject.isEmpty()) {
            for (String key : srcJSONObject.keySet()) {
                Object object = srcJSONObject.get(key);
                if (object instanceof JSONObject) {
                    ArrayList subkeys = new ArrayList<>();
                    if (keys != null) {
                        subkeys.addAll(keys);
                    }
                    subkeys.add(key);
                    createFlatJSONObject((JSONObject) object, destJSONObject, subkeys);
                } else {
                    if (keys == null || keys.isEmpty()) {
                        destJSONObject.put(key, object);
                    } else {
                        destJSONObject.put(StringUtils.join(new String[]{createPath(keys), key}, "."), object);
                    }
                }
            }
        }

        return destJSONObject;
    }

    synchronized public static Integer getCalculatedPixels(float size, float dpi) {
        return (int) (size * dpi * 2.83743842365f / 72);
    }

    synchronized public static float mmToPoints(float mm) {
        return mm * 2.83743842365f;
    }
    
    synchronized public static void validateLabelSettings(AddressLabel addressLabel, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        Integer labelRotation = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                "labelSettings.labelRotation", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (labelRotation > 360 || labelRotation < -360) {
            throw new ALGException(10027,
                    "Field 'label rotation' contains an invalid entry. Please provide a valid entry");
        }
        
        Integer barcodeDPI = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), "labelSettings.barcodeDPI", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (barcodeDPI < 0) {
            throw new ALGException(10027,
                    "Field 'barcode DPI' contains an invalid entry. Please provide a valid entry");
        }

        Integer qrcodeDPI = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), "labelSettings.barcodeDPI", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (qrcodeDPI < 0) {
            throw new ALGException(10027,
                    "Field 'qrcode DPI' contains an invalid entry. Please provide a valid entry");
        }

        Integer labelWidth = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), "labelSettings.labelWidth", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (labelWidth < 0) {
            throw new ALGException(10027,
                    "Field 'labelWidth' contains an invalid entry. Please provide a valid entry");
        }

        Integer labelHeight = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                "labelSettings.labelHeight", stringKeys, integerKeys, excludeFieldsFromValidation);
        if (labelHeight < 0) {
            throw new ALGException(10027,
                    "Field 'labelHeight' contains an invalid entry. Please provide a valid entry");
        }
    }
    
    synchronized public static void validateName(AddressLabel addressLabel, String key, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation, boolean includeCompanyName) {
        String name1 = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                key + ".name1", stringKeys, integerKeys, excludeFieldsFromValidation);
        String name2 = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                key + ".name2", stringKeys, integerKeys, excludeFieldsFromValidation);
        String name3 = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                key + ".name3", stringKeys, integerKeys, excludeFieldsFromValidation);

        boolean isAddressNameAvailable
                = !StringUtils.isBlank(name1) || !StringUtils.isBlank(name2) || !StringUtils.isBlank(name3);
        
        if (!isAddressNameAvailable && includeCompanyName){
            String company = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                key + ".company", stringKeys, integerKeys, excludeFieldsFromValidation);
            isAddressNameAvailable = !StringUtils.isBlank(company);
        }

        if (!isAddressNameAvailable) {
            throw new ALGException(10010, "Field '" + key + " name' is blank. Please provide a valid '" + key + " name'");
        }
    }
    
    synchronized public static float readPropertyAsFloat(Properties properties, String name) {
        LOG.debug("name={}", name);
        return Float.parseFloat(properties.getProperty(name));
    }

    synchronized public static float getCalculatedProperty(Properties properties, String name, float total) {
        LOG.debug("name={}", name);
        return total * readPropertyAsFloat(properties, name) / 100;
    }

    synchronized public static float getCalculatedProperty(Properties properties, String name, float totalWidth, float totalHeight) {
        LOG.debug("name={}", name);
        return totalWidth * totalHeight * readPropertyAsFloat(properties, name) / 100;
    }
}

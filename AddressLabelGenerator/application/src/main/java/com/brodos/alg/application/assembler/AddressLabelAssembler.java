
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.assembler;

import com.brodos.alg.application.dto.AddressLabelDTO;
import com.brodos.alg.application.dto.DHLDTO;
import com.brodos.alg.application.dto.FreightForwarderType;
import com.brodos.alg.application.dto.ProductType;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.entity.FreightForwarder;
import com.brodos.alg.domain.exception.ALGException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.message.Message;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class AddressLabelAssembler {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(AddressLabelAssembler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
    private static final StringBuilder URL_BUILDER = new StringBuilder();

    synchronized public static void removeNullCustomProperties(AddressLabelDTO addressLabelDTO) {
        try {
            List<String> keysWithNullValue = new ArrayList<>();
            Map<String, Object> map = addressLabelDTO.getFreightForwarder().getCustomProperties();
            for (String key : map.keySet()) {
                if (map.get(key) == null || StringUtils.isBlank(map.get(key).toString())) {
                    keysWithNullValue.add(key);
                }
            }

            for (String keyToRemove : keysWithNullValue) {
                map.remove(keyToRemove);
            }
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
        }
    }

    synchronized public static AddressLabel toAddressLabel(AddressLabelDTO addressLabelDTO, String version) {
        LOG.debug("Assembling AddressLabelDto to Domain Object");
        removeNullCustomProperties(addressLabelDTO);
        AddressLabel addressLabel = new AddressLabel();
        FreightForwarder freightForwarder = new FreightForwarder(addressLabelDTO.getFreightForwarder().getFreightForwarderType().toString());
        addressLabel.setFreightForwarder(freightForwarder);
        addressLabel.setTrackingCode(addressLabelDTO.getTrackingCode());
        addressLabel.setClient(addressLabelDTO.getClient());

        try {
            addressLabel.setRequestJson(new JSONObject(OBJECT_MAPPER.writeValueAsString(addressLabelDTO)));
            addressLabel.getRequestJson().put("VERSION", version);
            LOG.debug("After converting to requestJson = {}", addressLabel.getRequestJson());
        } catch (JsonProcessingException jpe) {
            LOG.trace(jpe.getMessage(), jpe);
            throw new ALGException(10032, "Unable to parse request body.");
        }

        if (addressLabelDTO.getFreightForwarder().getFreightForwarderType() == FreightForwarderType.DHL) {
            DHLDTO dhldto = (DHLDTO) addressLabelDTO.getFreightForwarder();
            if (dhldto.getProduct() == null) {
                if (addressLabel.getRequestJson().has("VERSION")
                        && addressLabel.getRequestJson().getString("VERSION").equalsIgnoreCase("v2")) {
                    throw new ALGException(10031, "Field 'product' is blank. Please provide a valid 'product'");
                }

                dhldto.setProduct(ProductType.DHL_RETURN);
                addressLabelDTO.setFreightForwarder(dhldto);

                if (addressLabel.getRequestJson().has("freightForwarder")) {
                    addressLabel.getRequestJson().getJSONObject("freightForwarder").put("product", "DHL_RETURN");
                }
            }
        }

        if (addressLabelDTO.getFreightForwarder().getFreightForwarderType() == FreightForwarderType.TOF && addressLabelDTO.getFreightForwarder().getCustomProperties().containsKey("specialServiceCode") && !(addressLabelDTO.getFreightForwarder().getCustomProperties().get("specialServiceCode") instanceof Integer)) {
            throw new ALGException(10023, "Field 'special service code' contains an invalid entry. Please provide a valid entry");
        }

        if (addressLabelDTO.getFreightForwarder().getFreightForwarderType() == FreightForwarderType.TOF && addressLabelDTO.getFreightForwarder().getCustomProperties().containsKey("specialServiceCode") && ((Integer) addressLabelDTO.getFreightForwarder().getCustomProperties().get("specialServiceCode") == 10)) {
            if (addressLabel.getRequestJson().has("deliveryTimestamp") && !StringUtils.isBlank(addressLabel.getRequestJson().getString("deliveryTimestamp"))) {
                try {
                    SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
                    addressLabel.setDeliveryTimestamp(SIMPLE_DATE_FORMAT.parse(addressLabel.getRequestJson().getString("deliveryTimestamp")));
                } catch (ParseException ex) {
                    LOG.trace(ex.getMessage(), ex);
                    throw new ALGException(10007, "Field 'delivery timestamp' contains an invalid entry. Please provide a valid entry", ex);
                }
            } else {
                throw new ALGException(10007, "Field 'delivery timestamp' is blank. Please provide a valid 'delivery timestamp'");
            }
        }

        LOG.debug("RequestJson={}", addressLabel.getRequestJson());

        return addressLabel;
    }

    synchronized public static void prepareResponse(Request request, AddressLabelDTO addressLabelDTO, AddressLabel addressLabel) {
        URL_BUILDER.setLength(0);
        URL_BUILDER.append(getRequestURLBasePath(request));
        if (!URL_BUILDER.toString().endsWith(addressLabel.getId().toString() + "/")) {
            URL_BUILDER.append(addressLabel.getId());
        }
        URL_BUILDER.append(".");
        URL_BUILDER.append(addressLabelDTO.getDocumentFormat().toLowerCase()).toString();

        addressLabelDTO.setSelf(URL_BUILDER.toString().replace("/.", "."));

        LOG.info("self={}", addressLabelDTO.getSelf());
        addressLabelDTO.setId(addressLabel.getId().toString());
        addressLabelDTO.setTrackingCode(addressLabel.getTrackingCode());
        if (addressLabel.getRequestJson().has("routingCode")) {
            addressLabelDTO.setRoutingCode(addressLabel.getRequestJson().getString("routingCode"));
            addressLabelDTO.getFreightForwarder().getCustomProperties().put("routingCode", addressLabelDTO.getRoutingCode());
        }

        LOG.info("self={}", addressLabelDTO.getSelf());
    }

    synchronized public static String getRequestURLBasePath(Request request) {
        try {
            Field messageField = request.getClass().getDeclaredField("m");
            messageField.setAccessible(true);
            Message message = (Message) messageField.get(request);
            String requestUrl = (String) message.get(Message.REQUEST_URL);
            if (!requestUrl.endsWith("/")) {
                requestUrl += "/";
            }

            if (StringUtils.countMatches(requestUrl, ":") == 1) {
                requestUrl = requestUrl.replace("http://", "https://");
            }

            return requestUrl;
        } catch (Exception ex) {
            LOG.trace(ex.getMessage(), ex);
        }

        return "";
    }

    synchronized public static AddressLabelDTO toAddressLabelDTO(Request request, AddressLabel addressLabel) {
        try {
            LOG.info("Request Json={}", addressLabel.getRequestJson());
            addressLabel.getRequestJson().remove("validatedSpecialServices");
            addressLabel.getRequestJson().remove("qrcode");
            addressLabel.getRequestJson().remove("validatedShippingType");
            addressLabel.getRequestJson().remove("distributionCenterCode");
            addressLabel.getRequestJson().remove("VERSION");
            addressLabel.getRequestJson().remove("barcode");
            addressLabel.getRequestJson().remove("namedPersonOnly");
            addressLabel.getRequestJson().remove("deliveryTimeRange");
            addressLabel.getRequestJson().remove("deliveryDate");
            AddressLabelDTO addressLabelDTO = OBJECT_MAPPER.readValue(addressLabel.getRequestJson().toString(), AddressLabelDTO.class);
            prepareResponse(request, addressLabelDTO, addressLabel);
            return addressLabelDTO;
        } catch (Exception ex) {
            LOG.trace(ex.getMessage(), ex);
            throw new ALGException(10034, "Unable to parse request json.", ex);
        }
    }

    synchronized public static List<AddressLabelDTO> toAddressLabelDTOs(Request request, List<AddressLabel> addressLabels) {
        List<AddressLabelDTO> addressLabelDTOs = new ArrayList<>();
        for (AddressLabel addressLabel : addressLabels) {
            addressLabelDTOs.add(toAddressLabelDTO(request, addressLabel));
        }

        return addressLabelDTOs;
    }
}

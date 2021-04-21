/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.adapter;

import com.brodos.alg.domain.util.XMLReaderWithoutNamespace;
import com.brodos.alg.dto.ContentCardResult;
import com.brodos.alg.dto.ContentDelivery;
import com.brodos.alg.dto.Timestamp;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class PORDeliveryAdapter {
    
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(PORDeliveryAdapter.class);

    private final String ppu;
    private final Unmarshaller unmarshaller;
    private final String trackingCodeAPIUrl;
    private final String password;

    public PORDeliveryAdapter(String ppu, String trackingCodeAPIUrl, String password) throws JAXBException {
        this.unmarshaller = JAXBContext.newInstance(ContentCardResult.class, ContentDelivery.class, Timestamp.class).createUnmarshaller();
        this.ppu = ppu;
        this.trackingCodeAPIUrl = trackingCodeAPIUrl;
        this.password = password;
    }

    // POR Delivery Request
    public ContentCardResult doPORDelivery(String bmcno, Integer cspid, String tan) throws UnirestException, JAXBException, XMLStreamException {
        GetRequest getRequest = Unirest.get(trackingCodeAPIUrl);
        Map<String, Object> requestParameters = generatePORDeliveryRequestParameters(bmcno, password, cspid, tan);
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            getRequest.queryString(entry.getKey(), entry.getValue().toString());
        }

        String responseBody = getRequest.asString().getBody();
        LOG.debug("responseBody={}", responseBody);
        
        XMLStreamReader xMLStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(new StreamSource(new StringReader(responseBody)));
        XMLReaderWithoutNamespace xMLReaderWithoutNamespace = new XMLReaderWithoutNamespace(xMLStreamReader);
        
        LOG.debug("XMLReaderWithoutNamespace={}", xMLReaderWithoutNamespace.toString());
        return unmarshaller.unmarshal(xMLReaderWithoutNamespace, ContentCardResult.class).getValue();
    }

    private Map<String, Object> generatePORDeliveryRequestParameters(String bmcno, String password, Integer cspid, String tan) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", bmcno);
        parameters.put("pass", password);
        parameters.put("cspid", cspid);
        parameters.put("action", "2");
        parameters.put("serviceid", "1");
        parameters.put("version", "1.6");
        parameters.put("ppu", ppu);
        parameters.put("barcodetype", "EAN13");
        parameters.put("tan", tan);
        parameters.put("macaddress", "00:00:00:00:00:01");
        return parameters;
    }

    @Override
    public String toString() {
        return "PORDeliveryAdapter{" + "trackingCodeAPIUrl=" + trackingCodeAPIUrl + ", password=" + password + '}';
    }        
}

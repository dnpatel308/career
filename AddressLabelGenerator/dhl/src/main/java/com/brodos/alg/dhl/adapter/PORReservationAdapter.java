/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.adapter;

import com.brodos.alg.domain.util.XMLReaderWithoutNamespace;
import com.brodos.alg.dto.ContentCardResult;
import com.brodos.alg.dto.ContentReservation;
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
public class PORReservationAdapter {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(PORReservationAdapter.class);

    private final String ppu;
    private final Unmarshaller unmarshaller;
    private final String trackingCodeAPIUrl;
    private final String password;

    public PORReservationAdapter(String ppu, String trackingCodeAPIUrl, String password) throws JAXBException {
        this.unmarshaller = JAXBContext.newInstance(ContentCardResult.class, ContentReservation.class, Timestamp.class).createUnmarshaller();
        this.trackingCodeAPIUrl = trackingCodeAPIUrl;
        this.password = password;
        this.ppu = ppu;
    }

    // POR Reservation Request
    public ContentCardResult doPORReservation(String bmcno, Integer cspid, String barcode) throws UnirestException, JAXBException, XMLStreamException {
        GetRequest getRequest = Unirest.get(trackingCodeAPIUrl);
        Map<String, Object> requestParameters = generatePORReservationRequestParameters(bmcno, password, cspid, barcode);
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            getRequest.queryString(entry.getKey(), entry.getValue().toString());
        }
        
        LOG.debug("trackingCodeAPIUrl={}", trackingCodeAPIUrl);

        String responseBody = getRequest.asString().getBody();
        LOG.debug("responseBody={}", responseBody);

        XMLStreamReader xMLStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(new StreamSource(new StringReader(responseBody)));
        XMLReaderWithoutNamespace xMLReaderWithoutNamespace = new XMLReaderWithoutNamespace(xMLStreamReader);

        LOG.debug("XMLReaderWithoutNamespace={}", xMLReaderWithoutNamespace.toString());
        return unmarshaller.unmarshal(xMLReaderWithoutNamespace, ContentCardResult.class).getValue();
    }

    private Map<String, Object> generatePORReservationRequestParameters(String bmcno, String password, Integer cspid, String barcode) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", bmcno);
        parameters.put("pass", password);
        parameters.put("cspid", cspid);
        parameters.put("action", "1");
        parameters.put("serviceid", "1");
        parameters.put("version", "1.6");
        parameters.put("ppu", ppu);
        parameters.put("barcodetype", "EAN13");
        parameters.put("barcode", barcode);
        parameters.put("macaddress", "00:00:00:00:00:01");
        return parameters;
    }

    @Override
    public String toString() {
        return "PORReservationAdapter{" + "trackingCodeAPIUrl=" + trackingCodeAPIUrl + ", password=" + password + '}';
    }
}

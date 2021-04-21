package com.brodos.alg.application.assembler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.application.dto.TofRouteFigureKeyDTO;
import com.brodos.alg.domain.entity.TofRouteFigureKey;

public class TOFAssembler {
    private static final Logger LOG = LoggerFactory.getLogger(TOFAssembler.class);

    public static List<TofRouteFigureKey> toTofRouteFigureKey(MultipartBody multipartBody) {
        List<TofRouteFigureKey> tofRouteFigureKeys = new ArrayList<>();
        try {
            LOG.info("Extracting file={}", multipartBody.getRootAttachment().getContentDisposition().getFilename());
            InputStream inputStream = multipartBody.getRootAttachment().getDataHandler().getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
            while (br.ready()) {
                String raw = br.readLine();
                TofRouteFigureKey tofRouteFigureKey = new TofRouteFigureKey();
                tofRouteFigureKey.setVehicleRegistrationCode(raw.substring(0, raw.length() - 44).trim());
                tofRouteFigureKey.setPostalRangeFrom(raw.substring(3, raw.length() - 38).trim());
                tofRouteFigureKey.setPostalRangeTo(raw.substring(9, raw.length() - 32).trim());
                tofRouteFigureKey.setDepotId(raw.substring(15, raw.length() - 29).trim());
                tofRouteFigureKey.setDepotSupplement(raw.substring(18, raw.length() - 27).trim());
                tofRouteFigureKey.setDepotAbbreviation(raw.substring(20, raw.length() - 25).trim());
                tofRouteFigureKey.setDepotLocation(raw.substring(22, raw.length() - 2).trim());
                tofRouteFigureKey.setCountryCallingCode(raw.substring(45, raw.length() - 0).trim());
                tofRouteFigureKeys.add(tofRouteFigureKey);
            }
            LOG.info("Extracting file and creating object completed for file={}",
                multipartBody.getRootAttachment().getContentDisposition().getFilename());
        } catch (Exception ex) {
            LOG.error("Error in file={}", ex);
        }
        return tofRouteFigureKeys;
    }

    public static TofRouteFigureKeyDTO fromRouteFigureKey(List<TofRouteFigureKey> tofRouteFigureKeys,
        int totalRecordToCreate) {
        TofRouteFigureKeyDTO tofRouteFigureKeyDTO = new TofRouteFigureKeyDTO();
        LOG.info("Total TofRouteKey created={}", tofRouteFigureKeys.size());
        if (!tofRouteFigureKeys.isEmpty()) {
            tofRouteFigureKeyDTO.setIsAllRecordCreated(false);
            if (totalRecordToCreate == tofRouteFigureKeys.size()) {
                tofRouteFigureKeyDTO.setIsAllRecordCreated(true);
            }
            tofRouteFigureKeyDTO.setTotalRecord(tofRouteFigureKeys.size());
            return tofRouteFigureKeyDTO;
        }
        tofRouteFigureKeyDTO.setIsAllRecordCreated(false);
        tofRouteFigureKeyDTO.setTotalRecord(tofRouteFigureKeys.size());
        return tofRouteFigureKeyDTO;
    }
}

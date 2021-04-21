/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.tof;

import com.brodos.alg.domain.LabelGenerator;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.entity.Countrykeys;
import com.brodos.alg.domain.entity.TofRouteFigureKey;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.alg.domain.util.Utils;
import com.brodos.alg.tof.util.TOFUtils;
import com.brodos.article.domain.service.DomainRegistryService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.json.JSONObject;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class TOFPdfLabelGenerator extends LabelGenerator {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(TOFPdfLabelGenerator.class);

    public float PAGE_HEIGHT;
    public float PAGE_WIDTH;

    private final Integer qrcodeVersion;

    private JSONObject validatedRequestJson;

    public TOFPdfLabelGenerator(Integer qrcodeVersion, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        super(stringKeys, integerKeys, excludeFieldsFromValidation);
        this.qrcodeVersion = qrcodeVersion;
    }

    @Override
    public void validateRequest(AddressLabel addressLabel) {
        Utils.validateLabelSettings(addressLabel, getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());

        String senderIsoCountryCode = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(), "sender.isoCountryCode", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
        if (senderIsoCountryCode.length() > 3) {
            throw new ALGException(10022, "Field 'sender country code' contains an invalid entry. Please provide a valid entry");
        }

        String recipientIsoCountryCode = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(), "recipient.isoCountryCode", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
        if (recipientIsoCountryCode.length() > 3) {
            throw new ALGException(10022, "Field 'recipient country code' contains an invalid entry. Please provide a valid entry");
        }

        String senderPostalCode = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(), "sender.postalCode", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
        if (senderPostalCode.length() > 5) {
            throw new ALGException(10021, "Field 'sender postal code' contains an invalid entry. Please provide a valid entry");
        }

        String recipientPostalCode = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(), "recipient.postalCode", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
        if (recipientPostalCode.length() > 5) {
            throw new ALGException(10021, "Field 'recipient postal code' contains an invalid entry. Please provide a valid entry");
        }

        Countrykeys countrykeys = DomainRegistryService.instance().countrykeysRepository().findCountrykeyByIsocodeAlpha2(senderIsoCountryCode);

        if (countrykeys == null) {
            countrykeys = DomainRegistryService.instance().countrykeysRepository().findCountrykeyByIsocodeAlpha3(senderIsoCountryCode);
        }

        if (countrykeys == null) {
            throw new ALGException(10024, "Field 'sender country code' contains an invalid entry. Please provide a valid entry");
        }

        String countryCode = countrykeys.getCountryCallingCode().replace("+", "");

        TofRouteFigureKey tofRouteFigureKey = DomainRegistryService.instance().tofRouteFigureKeyRepository().findByCountryCallingCodeAndPostalRange(countryCode, senderPostalCode);

        if (tofRouteFigureKey == null) {
            throw new ALGException(10025, "Field 'sender postal code' contains an invalid entry. Please provide a valid entry");
        }

        countrykeys = DomainRegistryService.instance().countrykeysRepository().findCountrykeyByIsocodeAlpha2(recipientIsoCountryCode);

        if (countrykeys == null) {
            countrykeys = DomainRegistryService.instance().countrykeysRepository().findCountrykeyByIsocodeAlpha3(recipientIsoCountryCode);
        }

        if (countrykeys == null) {
            throw new ALGException(10024, "Field 'recipient country code' contains an invalid entry. Please provide a valid entry");
        }

        countryCode = countrykeys.getCountryCallingCode().replace("+", "");

        tofRouteFigureKey = DomainRegistryService.instance().tofRouteFigureKeyRepository().findByCountryCallingCodeAndPostalRange(countryCode, recipientPostalCode);

        if (tofRouteFigureKey == null) {
            throw new ALGException(10025, "Field 'recipient postal code' contains an invalid entry. Please provide a valid entry");
        }

        addressLabel.getRequestJson().put("distributionCenterCode", tofRouteFigureKey.getDepotAbbreviation());
        String vehicleRegistrationCode = tofRouteFigureKey.getVehicleRegistrationCode();

        if (!addressLabel.getRequestJson().has("barcode")
                || StringUtils.isBlank(addressLabel.getRequestJson().getString("barcode"))) {
            addressLabel.getRequestJson().put("barcode", TOFUtils.generateBarcodeString(addressLabel.getRequestJson(), countryCode, getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
        }

        if (!addressLabel.getRequestJson().has("qrcode")
                || StringUtils.isBlank(addressLabel.getRequestJson().getString("qrcode"))) {
            addressLabel.getRequestJson().put("qrcode", TOFUtils.generateQRCodeString(addressLabel, qrcodeVersion, vehicleRegistrationCode, getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
        }

        Utils.validateName(addressLabel, "sender", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation(), true);
        Utils.validateName(addressLabel, "recipient", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation(), true);

        addressLabel.getRequestJson().put("validatedSpecialServices", TOFUtils.getValidatedSpecialServices(addressLabel.getRequestJson(), getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
        addressLabel.getRequestJson().put("validatedShippingType", TOFUtils.getValidatedShippingType(addressLabel.getRequestJson(), getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));

        if (!addressLabel.getRequestJson().has("trackingCode")
                || StringUtils.isBlank(addressLabel.getRequestJson().getString("trackingCode"))) {
            addressLabel.getRequestJson().put("trackingCode", addressLabel.getRequestJson().getString("barcode").replace("-", ""));
            addressLabel.setTrackingCode(addressLabel.getRequestJson().getString("barcode").replace("-", ""));
        }
    }

    @Override
    public AddressLabel generateLabel(AddressLabel addressLabel) throws ALGException {
        validatedRequestJson = Utils.createFlatJSONObject(addressLabel.getRequestJson(), null, null);
        LOG.debug("validatedRequestJson={}", validatedRequestJson);
        loadDocumentLayoutProperties(validatedRequestJson.getString("printSize").toLowerCase() + ".properties");

        AddressLabel existingAddressLabelForSameTrackingCode = DomainRegistryService.instance().addressLabelRepository().checkAddressLabelExistForTrackingCodeOrNot(addressLabel.getFreightForwarder().getKey(), validatedRequestJson.getString("trackingCode"), "application/pdf");

        if (existingAddressLabelForSameTrackingCode != null) {
            addressLabel.setId(existingAddressLabelForSameTrackingCode.getId());
        }

        createPdfLabel(addressLabel);
        return addressLabel;
    }

    public void loadDocumentLayoutProperties(String resourceName) {
        ClassLoader loader = TOFPdfLabelGenerator.class.getClassLoader();
        LOG.debug("resourceName={}", resourceName);
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            getDocumentLayoutProperties().load(resourceStream);
        } catch (Exception ex) {
            LOG.trace(ex.getMessage(), ex);
            throw new ALGException(10012, "Field 'print size' contains an invalid entry. Please provide a valid entry",
                    ex);
        }

        PAGE_HEIGHT = Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "document.height");
        PAGE_WIDTH = Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "document.width");
    }

    private void createPdfLabel(AddressLabel addressLabel) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            if (validatedRequestJson.has("labelSettings.labelWidth")) {
                Integer labelWidth = validatedRequestJson.getInt("labelSettings.labelWidth");
                if (labelWidth > 0) {
                    PAGE_WIDTH = Utils.mmToPoints(labelWidth);
                }
            }

            if (validatedRequestJson.has("labelSettings.labelHeight")) {
                Integer labelHeight = validatedRequestJson.getInt("labelSettings.labelHeight");
                if (labelHeight > 0) {
                    PAGE_HEIGHT = Utils.mmToPoints(labelHeight);;
                }
            }

            PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
            document.addPage(page);

            Integer labelRotation = 0;
            if (validatedRequestJson.has("labelSettings.labelRotation")) {
                labelRotation = validatedRequestJson.getInt("labelSettings.labelRotation");
                if (labelRotation > 0) {
                    page.setRotation(labelRotation);
                }
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                PDRectangle mediaBox = page.getMediaBox();
                mediaBox.setLowerLeftX(Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.left") * -1);
                mediaBox.setLowerLeftY(Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.top") * -1);
                mediaBox.setUpperRightX(PAGE_WIDTH + Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.right"));
                mediaBox.setUpperRightY(PAGE_HEIGHT + Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.bottom"));
                page.setMediaBox(mediaBox);

                drawBlocks(contentStream);

                // adding data block by block
                addQRCode(contentStream, validatedRequestJson.getString("qrcode"), addressLabel, document);
                addSpecialService(contentStream, validatedRequestJson.getString("validatedSpecialServices"));
                addSenderDetails(contentStream);
                addDistributionCenterCode(contentStream, validatedRequestJson.getString("distributionCenterCode"));
                addCountryISOCountryCode(contentStream, validatedRequestJson.getString("recipient.isoCountryCode"));
                addRecipientPostalCode(contentStream, validatedRequestJson.getString("recipient.postalCode"));
                addRecipientDetails(contentStream);

                addBarcode(contentStream, validatedRequestJson.getString("barcode"), addressLabel, document);

                if (validatedRequestJson.has("freightForwarder.customProperties.freeuse")) {
                    addFreeUseBlock(contentStream, validatedRequestJson.getString("freightForwarder.customProperties.freeuse"));
                }
                addShipmentType(contentStream, validatedRequestJson.getString("validatedShippingType"));
                addShipmentReferenceNumber(contentStream, validatedRequestJson.getString("freightForwarder.customProperties.shipmentReferenceNumber"));
                addParcelNumber(contentStream, validatedRequestJson.getInt("packageNoOutOfTotalPackages"));
                addPackageTransferDate(contentStream);
            }

            document.save(byteArrayOutputStream);
            addressLabel.setLabelRepresentation(byteArrayOutputStream.toByteArray());
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            if (!(exception instanceof ALGException)) {
                throw new ALGException(10001, "Unable to create pdf document", exception);
            } else {
                throw (ALGException) exception;
            }
        }
    }

    private void drawBlocks(PDPageContentStream contentStream) throws IOException {
        addRectangle(contentStream, "specialservice");
        addRectangle(contentStream, "senderdetails");
//        addRectangle(contentStream, "recipientdetails");
        addRectangle(contentStream, "distributioncentercode");
        addRectangle(contentStream, "isocode");
        addRectangle(contentStream, "recipientpostalcode");
        addRectangle(contentStream, "shipmentreferencenumber");
        addRectangle(contentStream, "parcelnumber");
        addRectangle(contentStream, "packagetransferdate");
//        addRectangle(contentStream, "freeuse");
//        addRectangle(contentStream, "shipmenttype");
//        addRectangle(contentStream, "qrcode");
//        addRectangle(contentStream, "barcode");
        contentStream.moveTo(Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.x", PAGE_WIDTH), Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.y", PAGE_WIDTH) - 0.4f);
        contentStream.lineTo(Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.width", PAGE_WIDTH) + Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.rect.width", PAGE_WIDTH), Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.y", PAGE_WIDTH) - 0.4f);
        contentStream.stroke();
    }

    private void addParagraph(PDPageContentStream contentStream, Map<Integer, Map.Entry<String, PDFont>> lines, PDFont font, float fontSize, float startX, float startY, float paragraphWidth, float paragraphHeight, boolean horizontallyCentered, boolean verticallyCentered) throws IOException {

        if (verticallyCentered) {
            float ymargin = paragraphHeight / 2;
            if (ymargin < 0) {
                ymargin *= -1;
            }

            startY -= ymargin;
            float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            startY += ((lineHeight / 2) * (lines.size() - 1));
            startY -= lineHeight / 3;
        }

        for (int i = 0; i < lines.size(); i++) {
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            if (lines.get(i).getValue() == null) {
                contentStream.setFont(font, fontSize);
            } else {
                contentStream.setFont(lines.get(i).getValue(), fontSize);
            }

            String line = lines.get(i).getKey();

            float lineWidth = font.getStringWidth(line) / 1000 * fontSize;
            float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            float xmargin = 0;
            float ymargin = 0;

            if (horizontallyCentered) {
                xmargin = (paragraphWidth - lineWidth) / 2;
            }

            ymargin = i * lineHeight;

            contentStream.newLineAtOffset(startX + xmargin, startY - ymargin);

            contentStream.showText(line);
            contentStream.endText();
        }
    }

    private Map.Entry<String, PDFont> createNewEntry(String key, PDFont value) {
        return new Map.Entry<String, PDFont>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public PDFont getValue() {
                return value;
            }

            @Override
            public PDFont setValue(PDFont font) {
                return value;
            }
        };
    }

    private void addSpecialService(PDPageContentStream contentStream, String specialService) throws IOException {
        String[] specialServices = specialService.split("\\|");
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();
        for (int i = 0; i < specialServices.length; i++) {
            lines.put(i, createNewEntry(specialServices[i], null));
        }

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "specialservice.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "specialservice.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "specialservice.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "specialservice.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "specialservice.rect.height", PAGE_HEIGHT),
                true,
                true
        );
    }

    private void addSenderDetails(PDPageContentStream contentStream) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(lines.size(), createNewEntry("Versender:", PDType1Font.HELVETICA));

        String company = validatedRequestJson.has("sender.company") ? validatedRequestJson.getString("sender.company") : "";
        String name1 = validatedRequestJson.has("sender.name1") ? validatedRequestJson.getString("sender.name1") : "";
        String name2 = validatedRequestJson.has("sender.name2") ? validatedRequestJson.getString("sender.name2") : "";
        String name3 = validatedRequestJson.has("sender.name3") ? validatedRequestJson.getString("sender.name3") : "";

        if (!StringUtils.isBlank(company)) {
            lines.put(lines.size(), createNewEntry(company, null));
        }

        if (!StringUtils.isBlank(name1) || !StringUtils.isBlank(name2) || !StringUtils.isBlank(name3)) {
            lines.put(lines.size(), createNewEntry(String.format("%s %s %s", name1, name2, name3).trim(), null));
        }

        lines.put(lines.size(), createNewEntry(String.format("%s %s", validatedRequestJson.getString("sender.street"), validatedRequestJson.getString("sender.houseNo")), null));
        lines.put(lines.size(), createNewEntry(String.format("%s-%s %s", validatedRequestJson.getString("sender.isoCountryCode"), validatedRequestJson.getString("sender.postalCode"), validatedRequestJson.getString("sender.city")), null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "senderdetails.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "senderdetails.paragraph.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "senderdetails.paragraph.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "senderdetails.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "senderdetails.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addDistributionCenterCode(PDPageContentStream contentStream, String distributionCenterCode) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry(distributionCenterCode, null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "distributioncentercode.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "distributioncentercode.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "distributioncentercode.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "distributioncentercode.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "distributioncentercode.rect.height", PAGE_HEIGHT),
                true,
                true
        );
    }

    private void addCountryISOCountryCode(PDPageContentStream contentStream, String isoCountryCode) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry(isoCountryCode, null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "isocode.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "isocode.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "isocode.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "isocode.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "isocode.rect.height", PAGE_HEIGHT),
                true,
                true
        );
    }

    private void addRecipientPostalCode(PDPageContentStream contentStream, String postalCode) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry(postalCode, null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientpostalcode.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientpostalcode.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientpostalcode.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientpostalcode.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientpostalcode.rect.height", PAGE_HEIGHT),
                true,
                true
        );
    }

    private void addQRCode(PDPageContentStream contentStream, String qrcode, AddressLabel addressLabel, PDDocument document) throws IOException, WriterException {
        PDImageXObject imageXObject = generateQrCodeImage(qrcode, addressLabel, document);
        contentStream.drawImage(imageXObject,
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "qrcode.image.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "qrcode.image.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "qrcode.image.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "qrcode.image.height", PAGE_HEIGHT)
        );
    }

    private void addRecipientDetails(PDPageContentStream contentStream) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(lines.size(), createNewEntry("Empfänger:", PDType1Font.HELVETICA));

        String company = validatedRequestJson.has("recipient.company") ? validatedRequestJson.getString("recipient.company") : "";
        String name1 = validatedRequestJson.has("recipient.name1") ? validatedRequestJson.getString("recipient.name1") : "";
        String name2 = validatedRequestJson.has("recipient.name2") ? validatedRequestJson.getString("recipient.name2") : "";
        String name3 = validatedRequestJson.has("recipient.name3") ? validatedRequestJson.getString("recipient.name3") : "";

        if (!StringUtils.isBlank(company)) {
            lines.put(lines.size(), createNewEntry(company, null));
        }

//        if (!StringUtils.isBlank(name1) || !StringUtils.isBlank(name2) || !StringUtils.isBlank(name3)) {
//            lines.put(lines.size(), createNewEntry(String.format("%s %s %s", name1, name2, name3).trim(), null));
//        }

        if (!StringUtils.isBlank(name1)) {
            lines.put(lines.size(), createNewEntry(name1, null));
        }

        if (!StringUtils.isBlank(name2)) {
            lines.put(lines.size(), createNewEntry(name2, null));
        }

        if (!StringUtils.isBlank(name3)) {
            lines.put(lines.size(), createNewEntry(name3, null));
        }

        lines.put(lines.size(), createNewEntry(String.format("%s %s", validatedRequestJson.getString("recipient.street"), validatedRequestJson.has("recipient.houseNo") ? validatedRequestJson.getString("recipient.houseNo") : ""), null));
        lines.put(lines.size(), createNewEntry(String.format("%s-%s %s", validatedRequestJson.getString("recipient.isoCountryCode"), validatedRequestJson.getString("recipient.postalCode"), validatedRequestJson.getString("recipient.city")), null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientdetails.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientdetails.paragraph.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientdetails.paragraph.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientdetails.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipientdetails.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addBarcode(PDPageContentStream contentStream, String barcode, AddressLabel addressLabel, PDDocument document) throws IOException, WriterException {
        PDImageXObject imageXObject = generateBarcodeImage(barcode, addressLabel, document);
        contentStream.drawImage(imageXObject,
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.image.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.image.y", PAGE_HEIGHT) + (PAGE_HEIGHT / 10),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.image.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.image.height", PAGE_HEIGHT) - (PAGE_HEIGHT / 10)
        );

        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry(barcode, null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "barcode.rect.height", PAGE_HEIGHT),
                true,
                false
        );
    }

    private void addFreeUseBlock(PDPageContentStream contentStream, String freeuse) throws IOException {
        if (!StringUtils.isBlank(freeuse)) {
            Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();
            if (freeuse.contains("|")) {
                String[] freeuses = freeuse.split("\\|");
                lines.put(lines.size(), createNewEntry(freeuses[0], PDType1Font.HELVETICA));
                lines.put(lines.size(), createNewEntry(freeuses[1], null));
            } else {
                lines.put(lines.size(), createNewEntry(freeuse, null));
            }

            addParagraph(contentStream,
                    lines,
                    PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                    Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.paragraph.x", PAGE_WIDTH),
                    Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.paragraph.y", PAGE_HEIGHT),
                    Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.rect.width", PAGE_WIDTH),
                    Utils.getCalculatedProperty(getDocumentLayoutProperties(), "freeuse.rect.height", PAGE_HEIGHT),
                    false,
                    false
            );
        }
    }

    private void addShipmentType(PDPageContentStream contentStream, String shipmentType) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        if (shipmentType.equalsIgnoreCase("national")) {
            lines.put(0, createNewEntry("trans-o-flex", null));
        } else if (shipmentType.equalsIgnoreCase("international")) {
            lines.put(0, createNewEntry("Eurodis", null));
        }

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA_BOLD, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmenttype.label.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmenttype.label.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmenttype.label.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmenttype.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmenttype.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addShipmentReferenceNumber(PDPageContentStream contentStream, String shipmentReferenceNumber) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry("Ref.Nr.", null));
        lines.put(1, createNewEntry(shipmentReferenceNumber, null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmentreferencenumber.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmentreferencenumber.paragraph.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmentreferencenumber.paragraph.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmentreferencenumber.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "shipmentreferencenumber.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addParcelNumber(PDPageContentStream contentStream, Integer packageNoOutOfTotalPackages) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry("Paket.Nr.", null));
        lines.put(1, createNewEntry(packageNoOutOfTotalPackages.toString(), null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "parcelnumber.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "parcelnumber.paragraph.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "parcelnumber.paragraph.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "parcelnumber.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "parcelnumber.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addPackageTransferDate(PDPageContentStream contentStream) throws IOException {
        Map<Integer, Map.Entry<String, PDFont>> lines = new HashMap<>();

        lines.put(0, createNewEntry("Datum", null));
        lines.put(1, createNewEntry(new SimpleDateFormat("dd.MM.yyyy").format(new Date()), null));

        addParagraph(contentStream,
                lines,
                PDType1Font.HELVETICA, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "packagetransferdate.paragraph.font.size", PAGE_WIDTH, PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "packagetransferdate.paragraph.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "packagetransferdate.paragraph.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "packagetransferdate.rect.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "packagetransferdate.rect.height", PAGE_HEIGHT),
                false,
                false
        );
    }

    private void addRectangle(PDPageContentStream contentStream, String propName) throws IOException {
        float x = Utils.getCalculatedProperty(getDocumentLayoutProperties(), propName + ".rect.x", PAGE_WIDTH);
        float y = Utils.getCalculatedProperty(getDocumentLayoutProperties(), propName + ".rect.y", PAGE_HEIGHT);
        float width = Utils.getCalculatedProperty(getDocumentLayoutProperties(), propName + ".rect.width", PAGE_WIDTH);
        float height = Utils.getCalculatedProperty(getDocumentLayoutProperties(), propName + ".rect.height", PAGE_HEIGHT);
        float lineWidth = Utils.readPropertyAsFloat(getDocumentLayoutProperties(), propName + ".rect.linewidth");

        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setLineWidth(lineWidth);
        contentStream.addRect(x, y, width, height);
        contentStream.fillAndStroke();
    }

    private PDImageXObject generateBarcodeImage(String barcodeNumber, AddressLabel addressLabel, PDDocument document) throws IOException {
        Integer barcodeDPI = validatedRequestJson.getInt("labelSettings.barcodeDPI");
        if (barcodeDPI == 0) {
            barcodeDPI = 1200;
        }

        Interleaved2Of5Bean bean = new Interleaved2Of5Bean();
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        bean.setWideFactor(2.5f);
        bean.setModuleWidth(0.5264);
        BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(barcodeDPI, BufferedImage.TYPE_INT_RGB, false, 0);

        bean.generateBarcode(canvasProvider, barcodeNumber.replace("-", ""));
        canvasProvider.finish();

        synchronized (TOFPdfLabelGenerator.class) {
            PDImageXObject imageXObject;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIOUtil.writeImage(canvasProvider.getBufferedImage(), "png", baos);
                imageXObject = PDImageXObject.createFromByteArray(document, baos.toByteArray(), addressLabel.getRequestJson().get("trackingCode") + "_barcode.png");
            }
            return imageXObject;
        }
    }

    private PDImageXObject generateQrCodeImage(String content, AddressLabel addressLabel, PDDocument document) throws IOException, WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.QR_VERSION, 14);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();

        Integer qrcodeDPI = validatedRequestJson.getInt("labelSettings.qrcodeDPI");
        if (qrcodeDPI == 0) {
            qrcodeDPI = 504;
        }

        Integer qrcodeCalculatedPixels = Utils.getCalculatedPixels(43, qrcodeDPI); // qrcode block: 47x47

        LOG.debug("qrcodeDPI={}", qrcodeDPI);
        LOG.debug("qrcodeCalculatedPixels={}", qrcodeCalculatedPixels);

        BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrcodeCalculatedPixels, qrcodeCalculatedPixels, hints);

        BufferedImage image = new BufferedImage(byteMatrix.getWidth(), byteMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, byteMatrix.getWidth(), byteMatrix.getHeight());

        graphics.setColor(java.awt.Color.BLACK);

        for (int x = 0; x < byteMatrix.getWidth(); x++) {
            for (int y = 0; y < byteMatrix.getHeight(); y++) {
                if (byteMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        synchronized (TOFPdfLabelGenerator.class) {
            PDImageXObject imageXObject;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIOUtil.writeImage(image, "png", baos);
                imageXObject = PDImageXObject.createFromByteArray(document, baos.toByteArray(), addressLabel.getRequestJson().get("trackingCode") + "_qrcode.png");
            }
            return imageXObject;
        }
    }
}

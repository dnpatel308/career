/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
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
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.dhl.service.DhlRoutingCodeService;
import com.brodos.alg.dhl.service.DhlTrackingCodeService;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.entity.DhlTimerangeCodes;
import com.brodos.alg.domain.entity.FreightForwarderClientConfig;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.alg.domain.util.Utils;
import com.brodos.article.domain.service.DomainRegistryService;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import java.text.SimpleDateFormat;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author padhaval
 */
public class DHLPdfLabelGenerator extends DHLLabelGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(DHLPdfLabelGenerator.class);
    public static final Color BLACK_COLOR = new DeviceCmyk(1f, 1f, 1f, 1f);
    public float PAGE_HEIGHT;
    public float PAGE_WIDTH;

    private JSONObject validatedRequestJson;
    private JSONObject clientConfigurationsJson;

    private final Map<String, List<String>> clientSpecificExcludes;

    public DHLPdfLabelGenerator(DhlTrackingCodeService trackingService, DhlRoutingCodeService routingCodeService,
        List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation,
        Map<String, List<String>> clientSpecificExcludes) {
        super(trackingService, routingCodeService, stringKeys, integerKeys, excludeFieldsFromValidation);
        this.clientSpecificExcludes = clientSpecificExcludes;
    }

    @Override
    public AddressLabel generateLabel(AddressLabel addressLabel) {
        validatedRequestJson = Utils.createFlatJSONObject(addressLabel.getRequestJson(), null, null);
        LOG.debug("validatedRequestJson={}", validatedRequestJson);
        loadDocumentLayoutProperties(validatedRequestJson.getString("printSize").toLowerCase() + ".properties");

        String routingCode = validatedRequestJson.getString("routingCode");
        String trackingCode = validatedRequestJson.getString("trackingCode");

        AddressLabel existingAddressLabelForSameTrackingCode =
            DomainRegistryService.instance().addressLabelRepository().checkAddressLabelExistForTrackingCodeOrNot(
                addressLabel.getFreightForwarder().getKey(), trackingCode, "application/pdf");

        if (existingAddressLabelForSameTrackingCode != null) {
            addressLabel.setId(existingAddressLabelForSameTrackingCode.getId());
        }

        createPdfLabel(addressLabel, routingCode, trackingCode);
        return addressLabel;
    }

    public void loadDocumentLayoutProperties(String resourceName) {
        ClassLoader loader = DHLPdfLabelGenerator.class.getClassLoader();
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

    public void loadClientConfigurations(String freightForwarder, String client) {
        clientConfigurationsJson = new JSONObject();
        List<FreightForwarderClientConfig> clientConfigurations =
            DomainRegistryService.instance().freightForwarderClientConfigRepository()
                .findByFreightForwarderAndClient(freightForwarder, StringUtils.isBlank(client) ? "BLM" : client);
        for (FreightForwarderClientConfig forwarderClientConfig : clientConfigurations) {
            clientConfigurationsJson.put(forwarderClientConfig.getKey(), forwarderClientConfig.getValue());
        }
    }

    public void createPdfLabel(AddressLabel addressLabel, String routingCode, String trackingCode) {
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
                    PAGE_HEIGHT = Utils.mmToPoints(labelHeight);
                    ;
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

            try (PDPageContentStream contentStream =
                new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                PDRectangle mediaBox = page.getMediaBox();
                mediaBox
                    .setLowerLeftX(Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.left") * -1);
                mediaBox
                    .setLowerLeftY(Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.top") * -1);
                mediaBox.setUpperRightX(
                    PAGE_WIDTH + Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.right"));
                mediaBox.setUpperRightY(
                    PAGE_HEIGHT + Utils.readPropertyAsFloat(getDocumentLayoutProperties(), "page.margin.bottom"));
                page.setMediaBox(mediaBox);

                addHeader(contentStream, document);
                // addCustomerLogo(contentStream, document);
                addSenderDetail(contentStream);
                addRecipientDetail(contentStream);
                addProductFeatures(contentStream);
                addLeitcode(contentStream, document, routingCode);
                addIdentcode(contentStream, document, trackingCode);
                addSoftwareDetail(contentStream);
                addServiceBlock(contentStream, document);
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

    private void addParagraph(PDPageContentStream contentStream,
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines, PDFont font, float fontSize, float startX,
        float startY, float paragraphWidth, float paragraphHeight, boolean horizontallyCentered,
        boolean verticallyCentered, float lineSpacing)
        throws IOException {

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
            contentStream.setNonStrokingColor(java.awt.Color.BLACK);
            if (!lines.get(i).getValue().containsKey("font")) {
                contentStream.setFont(font, fontSize);
            } else {
                contentStream.setFont((PDFont) lines.get(i).getValue().get("font"), fontSize);
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

            if (lines.get(i).getValue().containsKey("lineXmargin")) {
                xmargin += (float) lines.get(i).getValue().get("lineXmargin");
            }

            ymargin += i * lineSpacing;

            contentStream.newLineAtOffset(startX + xmargin, startY - ymargin);

            contentStream.showText(line);
            contentStream.endText();
        }
    }

    private Map<Integer, Map.Entry<String, Map<String, Object>>>
        createNewEntry(Map<Integer, Map.Entry<String, Map<String, Object>>> lines, String key) {
        return createNewEntry(lines, key, null, null);
    }

    private Map<Integer, Map.Entry<String, Map<String, Object>>> createNewEntry(
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines, String key, String subProp, Object subPropValue) {
        Integer index = lines.size();
        Map.Entry<String, Map<String, Object>> entry = null;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getKey().equalsIgnoreCase(key)) {
                entry = lines.get(i);
                index = i;
                break;
            }
        }

        if (entry == null) {
            entry = new Map.Entry<String, Map<String, Object>>() {
                private final HashMap<String, Object> map = new HashMap<>();

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public Map<String, Object> getValue() {
                    return map;
                }

                @Override
                public Map<String, Object> setValue(Map<String, Object> value) {
                    map.putAll(value);
                    return map;
                }
            };
        }

        if (subProp != null && subPropValue != null) {
            entry.getValue().put(subProp, subPropValue);
        }

        lines.put(index, entry);
        return lines;
    }

    private void
        addHeader(PDPageContentStream contentStream, PDDocument document) throws MalformedURLException, IOException {
        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.first.y", PAGE_HEIGHT), 1);

        PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document,
            IOUtils.toByteArray(this.getClass().getResource("/DHL.png")), "DHLLogo");
        contentStream.drawImage(imageXObject,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.logo.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.logo.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.logo.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.logo.height", PAGE_HEIGHT));

        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        createNewEntry(lines, getDocumentLayoutProperties().getProperty("dhl.product.name"));

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.product.name.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.product.name.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.product.name.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.product.name.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "dhl.product.name.height", PAGE_HEIGHT), false,
            true, 0);
    }

    private void addCustomerLogo(PDPageContentStream contentStream, PDDocument document) throws IOException {
        PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document,
            IOUtils.toByteArray(this.getClass().getResource("/CustomerLogo.png")), "CustomerLogo");
        contentStream.drawImage(imageXObject,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "customer.logo.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "customer.logo.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "customer.logo.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "customer.logo.height", PAGE_HEIGHT));

    }

    private void addSenderDetail(PDPageContentStream contentStream) throws IOException {
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        String headerString = "Von: ";

        String name1 = validatedRequestJson.getString("sender.name1");

        boolean isAddressNameAvailable = false;
        if (!StringUtils.isBlank(name1)) {
            createNewEntry(lines, headerString + name1);
            isAddressNameAvailable = true;
        }

        float lineXmargin = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "address.x.margin", PAGE_WIDTH);

        if (validatedRequestJson.has("sender.name3")) {
            String name2 = validatedRequestJson.getString("sender.name2");
            if (!StringUtils.isBlank(name2)) {
                if (isAddressNameAvailable) {
                    createNewEntry(lines, name2, "lineXmargin", lineXmargin);
                } else {
                    createNewEntry(lines, headerString + name2);
                    isAddressNameAvailable = true;
                }
            }
        }

        if (validatedRequestJson.has("sender.name3")) {
            String name3 = validatedRequestJson.getString("sender.name3");
            if (!StringUtils.isBlank(name3)) {
                if (isAddressNameAvailable) {
                    createNewEntry(lines, name3, "lineXmargin", lineXmargin);
                } else {
                    createNewEntry(lines, headerString + name3);
                    isAddressNameAvailable = true;
                }
            }
        }

        String streetAndHouseNo = String.format("%s, %s", validatedRequestJson.getString("sender.street"),
            validatedRequestJson.getString("sender.houseNo"));
        createNewEntry(lines, streetAndHouseNo, "lineXmargin", lineXmargin);
        String postalCodeAndCity = String.format("%s %s", validatedRequestJson.getString("sender.postalCode"),
            validatedRequestJson.getString("sender.city"));
        createNewEntry(lines, postalCodeAndCity, "lineXmargin", lineXmargin);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.address.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.address.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.address.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.address.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.address.height", PAGE_HEIGHT), false,
            false, 3.0f);

        lines.clear();
        
        if (validatedRequestJson.has("sender.phoneNo")) {
            String phoneNo = validatedRequestJson.getString("sender.phoneNo");
            createNewEntry(lines, "Absender Telefon:");
            createNewEntry(lines, phoneNo);

            addParagraph(contentStream, lines, PDType1Font.HELVETICA,
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.contact.font.size", PAGE_WIDTH,
                    PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.contact.x", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.contact.y", PAGE_HEIGHT),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.contact.width", PAGE_WIDTH),
                Utils.getCalculatedProperty(getDocumentLayoutProperties(), "sender.contact.height", PAGE_HEIGHT), true,
                false, 3.0f);
        }
    }

    private void drawLine(PDPageContentStream contentStream, float startx, float endx, float y, float lineWidth)
        throws IOException {
        y += lineWidth / 2;
        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(lineWidth);
        contentStream.moveTo(startx, y);
        contentStream.lineTo(endx, y);
        contentStream.closePath();
        contentStream.stroke();
    }

    private void addBox(PDPageContentStream contentStream, float boxX, float boxY, float boxHeight, float boxWidth,
        float boxCornerSize)
        throws IOException {
        // adding first corner of box
        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.moveTo(boxX, boxY - boxCornerSize);
        contentStream.lineTo(boxX, boxY);
        contentStream.lineTo(boxX + boxCornerSize, boxY);
        contentStream.moveTo(boxX, boxY - boxCornerSize);
        contentStream.closePath();
        contentStream.stroke();

        // adding second corner of box
        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.moveTo(boxX + boxWidth, boxY - boxCornerSize);
        contentStream.lineTo(boxX + boxWidth, boxY);
        contentStream.lineTo(boxX + boxWidth - boxCornerSize, boxY);
        contentStream.moveTo(boxX + boxWidth, boxY - boxCornerSize);
        contentStream.closePath();
        contentStream.stroke();

        // adding third corner of box
        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.moveTo(boxX + boxWidth, boxY - boxHeight + boxCornerSize);
        contentStream.lineTo(boxX + boxWidth, boxY - boxHeight);
        contentStream.lineTo(boxX + boxWidth - boxCornerSize, boxY - boxHeight);
        contentStream.moveTo(boxX + boxWidth, boxY - boxHeight + boxCornerSize);
        contentStream.closePath();
        contentStream.stroke();

        // adding fourth corner of box
        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.moveTo(boxX, boxY - boxHeight + boxCornerSize);
        contentStream.lineTo(boxX, boxY - boxHeight);
        contentStream.lineTo(boxX + boxCornerSize, boxY - boxHeight);
        contentStream.moveTo(boxX, boxY - boxHeight + boxCornerSize);
        contentStream.closePath();
        contentStream.stroke();
    }

    private void addRecipientDetail(PDPageContentStream contentStream) throws IOException {
        // adding recipient address block corners
        addBox(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "box.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "box.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "box.height", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "box.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "box.corner.size", PAGE_WIDTH, PAGE_HEIGHT));

        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        String headerString = "  An: ";

        String name1 = validatedRequestJson.getString("recipient.name1");

        boolean isAddressNameAvailable = false;
        if (!StringUtils.isBlank(name1)) {
            createNewEntry(lines, headerString + name1);
            isAddressNameAvailable = true;
        }

        float lineXmargin = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "address.x.margin", PAGE_WIDTH);

        if (validatedRequestJson.has("recipient.name2")) {
            String name2 = validatedRequestJson.getString("recipient.name2");
            if (!StringUtils.isBlank(name2)) {
                if (isAddressNameAvailable) {
                    createNewEntry(lines, name2, "lineXmargin", lineXmargin);
                } else {
                    createNewEntry(lines, headerString + name2);
                    isAddressNameAvailable = true;
                }
            }
        }

        if (validatedRequestJson.has("recipient.name3")) {
            String name3 = validatedRequestJson.getString("recipient.name3");
            if (!StringUtils.isBlank(name3)) {
                if (isAddressNameAvailable) {
                    createNewEntry(lines, name3, "lineXmargin", lineXmargin);
                } else {
                    createNewEntry(lines, headerString + name3);
                    isAddressNameAvailable = true;
                }
            }
        }

        if (!isAddressNameAvailable) {
            throw new ALGException(10010, "Field 'recipient name' is blank. Please provide a valid 'recipient name'");
        }

        String streetAndHouseNo = String.format("%s, %s", validatedRequestJson.getString("recipient.street"),
            validatedRequestJson.getString("recipient.houseNo"));
        createNewEntry(lines, streetAndHouseNo, "lineXmargin", lineXmargin);
        String postalCodeAndCity = String.format("%s %s", validatedRequestJson.getString("recipient.postalCode"),
            validatedRequestJson.getString("recipient.city"));
        createNewEntry(lines, postalCodeAndCity, "lineXmargin", lineXmargin);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.address.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.address.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.address.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.address.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.address.height", PAGE_HEIGHT), false,
            false, 3.0f);

        lines.clear();

        // String phoneNo = validatedRequestJson.getString("recipient.phoneNo");
        // if (!StringUtils.isBlank(phoneNo)) {
        // createNewEntry(lines, "Empfänger Telefon:");
        // createNewEntry(lines, phoneNo);
        //
        // addParagraph(contentStream, lines, PDType1Font.HELVETICA,
        // Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.contact.font.size", PAGE_WIDTH,
        // PAGE_HEIGHT),
        // Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.contact.x", PAGE_WIDTH),
        // Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.contact.y", PAGE_HEIGHT),
        // Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.contact.width", PAGE_WIDTH),
        // Utils.getCalculatedProperty(getDocumentLayoutProperties(), "recipient.contact.height", PAGE_HEIGHT), true,
        // false, 3.0f);
        // }
    }

    private void addProductFeatures(PDPageContentStream contentStream) throws IOException {
        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.second.y", PAGE_HEIGHT), 1);

        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.bar.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.third.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.bar.height", PAGE_HEIGHT));

        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.third.y", PAGE_HEIGHT), 1);

        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.fourth.y", PAGE_HEIGHT), 1);

        String referenceNo = validatedRequestJson.has("freightForwarder.customProperties.referenceNo")
            ? validatedRequestJson.getString("freightForwarder.customProperties.referenceNo") : "";

        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        createNewEntry(lines, "Abrechnungsnr:");
        if (!StringUtils.isBlank(referenceNo)) {
            createNewEntry(lines, "Referenznr:");
        }
        createNewEntry(lines, "Sendungsnr:", "font", PDType1Font.HELVETICA_BOLD);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.col1.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.height", PAGE_HEIGHT),
            false, false, 2.0f);

        lines.clear();

        String billingNo = validatedRequestJson.getString("freightForwarder.customProperties.billingNo");
        String shipmentReferenceNumber =
            validatedRequestJson.has("freightForwarder.customProperties.shipmentReferenceNumber")
                ? validatedRequestJson.getString("freightForwarder.customProperties.shipmentReferenceNumber") : "";

        if (StringUtils.isBlank(shipmentReferenceNumber)) {
            shipmentReferenceNumber = validatedRequestJson.getString("trackingCode");
        }

        float col2LineXmargin =
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.col1.width", PAGE_WIDTH);
        createNewEntry(lines, billingNo, "lineXmargin", col2LineXmargin);

        if (!StringUtils.isBlank(referenceNo)) {
            createNewEntry(lines, referenceNo, "lineXmargin", col2LineXmargin);
        }

        createNewEntry(lines, shipmentReferenceNumber, "lineXmargin", col2LineXmargin);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.col1.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.height", PAGE_HEIGHT),
            false, false, 2.0f);

        lines.clear();

        String weightString =
            String.format("%s %s", validatedRequestJson.getInt("weight.weightInIntegerRepresentation"),
                validatedRequestJson.getString("weight.unit"));

        float col3LineXmargin = col2LineXmargin + Utils.getCalculatedProperty(getDocumentLayoutProperties(),
            "product.feature.table.col2.width", PAGE_WIDTH);
        createNewEntry(lines, "Gewicht:", "lineXmargin", col3LineXmargin);
        createNewEntry(lines, weightString, "font", PDType1Font.HELVETICA_BOLD);
        createNewEntry(lines, weightString, "lineXmargin", col3LineXmargin);
        if (!StringUtils.isBlank(referenceNo)) {
            createNewEntry(lines, "-", "lineXmargin", col3LineXmargin);
        }

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.col1.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.height", PAGE_HEIGHT),
            false, false, 2.0f);

        lines.clear();

        String packageString = String.format("%d/%d", validatedRequestJson.getInt("packageNoOutOfTotalPackages"),
            validatedRequestJson.getInt("totalNoOfPackages"));

        float col4LineXmargin = col3LineXmargin + Utils.getCalculatedProperty(getDocumentLayoutProperties(),
            "product.feature.table.col3.width", PAGE_WIDTH);
        createNewEntry(lines, "Anzahl:", "lineXmargin", col4LineXmargin);
        createNewEntry(lines, packageString, "font", PDType1Font.HELVETICA_BOLD);
        createNewEntry(lines, packageString, "lineXmargin", col4LineXmargin);
        if (!StringUtils.isBlank(referenceNo)) {
            createNewEntry(lines, "-", "lineXmargin", col4LineXmargin);
        }

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.col1.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "product.feature.table.height", PAGE_HEIGHT),
            false, false, 2.0f);
    }

    private void addLeitcode(PDPageContentStream contentStream, PDDocument document, String leitcode)
        throws MalformedURLException,
            IOException {
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        createNewEntry(lines, "Leitcode/Routing Code");

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.header.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.header.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.header.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.header.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.header.height", PAGE_HEIGHT), false,
            false, 0);

        lines.clear();

        PDImageXObject imageXObject = generateBarcodeImage(leitcode, document, "routingcode");
        contentStream.drawImage(imageXObject,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.y", PAGE_HEIGHT) + (PAGE_HEIGHT / 10),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.height", PAGE_HEIGHT)
                - (PAGE_HEIGHT / 10));

        createNewEntry(lines, leitcode);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.label.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.label.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.label.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.label.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "leitcode.label.height", PAGE_HEIGHT), true,
            false, 0);
    }

    private void addIdentcode(PDPageContentStream contentStream, PDDocument document, String identcode)
        throws IOException {
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        createNewEntry(lines, "Identcode/License Plate");

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.header.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.header.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.header.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.header.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.header.height", PAGE_HEIGHT), false,
            false, 0);

        lines.clear();

        PDImageXObject imageXObject = generateBarcodeImage(identcode, document, "identcode");
        contentStream.drawImage(imageXObject,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.y", PAGE_HEIGHT) + (PAGE_HEIGHT / 10),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.height", PAGE_HEIGHT)
                - (PAGE_HEIGHT / 10));

        createNewEntry(lines, identcode);

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.label.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.label.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.label.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.label.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "identcode.label.height", PAGE_HEIGHT), true,
            false, 0);
    }

    private void addSoftwareDetail(PDPageContentStream contentStream) throws IOException {
        Map<Integer, Map.Entry<String, Map<String, Object>>> lines = new HashMap<>();

        createNewEntry(lines, getDocumentLayoutProperties().getProperty("software.label"));

        addParagraph(contentStream, lines, PDType1Font.HELVETICA,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "softwate.label.font.size", PAGE_WIDTH,
                PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "softwate.label.x", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "softwate.label.y", PAGE_HEIGHT),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "softwate.label.width", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "softwate.label.height", PAGE_HEIGHT), false,
            false, 0);
    }

    private PDImageXObject generateBarcodeImage(String barcodeNumber, PDDocument document, String barcodeFor)
        throws IOException {
        barcodeNumber = barcodeNumber.replaceAll("[^\\d.+]", "").replace(".", "").replace(" ", "");

        Integer barcodeDPI = 0;
        if (validatedRequestJson.has("labelSettings.barcodeDPI")) {
            barcodeDPI = validatedRequestJson.getInt("labelSettings.barcodeDPI");
        }

        if (barcodeDPI == 0) {
            barcodeDPI = 1200;
        }

        AbstractBarcodeBean bean = null;

        switch (validatedRequestJson.getString("freightForwarder.product")) {
            case "":
            case "DHL_RETURN": {
                bean = new Interleaved2Of5Bean();
                break;
            }

            case "DHL_STANDARD": {
                bean = new EAN128Bean();
                bean.setModuleWidth(0.5);
                if (barcodeFor.equalsIgnoreCase("identcode")) {
                    String client = validatedRequestJson.getString("client");
                    if (client.equalsIgnoreCase("TELEKOM1")) {
                        bean = new Interleaved2Of5Bean();
                    }
                }
                break;
            }
        }

        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        BitmapCanvasProvider canvasProvider =
            new BitmapCanvasProvider(barcodeDPI, BufferedImage.TYPE_INT_RGB, false, 0);
        bean.generateBarcode(canvasProvider, barcodeNumber);
        canvasProvider.finish();
        PDImageXObject imageXObject;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIOUtil.writeImage(canvasProvider.getBufferedImage(), "jpg", baos, barcodeDPI);
            imageXObject =
                PDImageXObject.createFromByteArray(document, baos.toByteArray(), barcodeNumber + "_barcode.jpg");
        }

        return imageXObject;
    }

    private void addServiceBlock(PDPageContentStream contentStream, PDDocument document) throws IOException {
        if (getDocumentLayoutProperties().get("line.fifth.y") == null
            || getDocumentLayoutProperties().get("line.sixth.y") == null) {
            return;
        }

        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.fifth.y", PAGE_HEIGHT), 1);

        drawLine(contentStream, Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.startx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.endx", PAGE_WIDTH),
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.sixth.y", PAGE_HEIGHT), 1);

        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(1);
        contentStream.moveTo(95.0f,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.fifth.y", PAGE_HEIGHT));
        contentStream.lineTo(95.0f,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.sixth.y", PAGE_HEIGHT));
        contentStream.closePath();
        contentStream.stroke();

        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(1);
        contentStream.moveTo(205.0f,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.fifth.y", PAGE_HEIGHT));
        contentStream.lineTo(205.0f,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.sixth.y", PAGE_HEIGHT));
        contentStream.closePath();
        contentStream.stroke();

        if (validatedRequestJson.has("deliveryTimeRange")) {
            drawTimeRange(contentStream);
        }

        if (validatedRequestJson.has("namedPersonOnly") && (boolean) validatedRequestJson.get("namedPersonOnly")) {
            drawNamedPersionOnlyBlock(contentStream);
        }

        if (validatedRequestJson.has("deliveryDate")) {
            drawDeliveryDate(contentStream);
        }
    }

    private void drawTimeRange(PDPageContentStream contentStream) throws IOException {

        float x = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.x", PAGE_WIDTH);
        float y = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.y", PAGE_HEIGHT);

        contentStream.stroke();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.fillRect(x, y - 20, x + 80, 40);
        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 32);
        contentStream.newLineAtOffset(x + 10, y - 12);
        contentStream.showText("ZEIT");
        contentStream.endText();
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE, 20);
        contentStream.newLineAtOffset(x + 156,
            Utils.getCalculatedProperty(getDocumentLayoutProperties(), "line.second.y", PAGE_HEIGHT) - 18);
        contentStream.showText(validatedRequestJson.getString("deliveryTimeRange"));
        contentStream.endText();
        contentStream.stroke();
    }

    private void drawNamedPersionOnlyBlock(PDPageContentStream contentStream) throws IOException {
        float x = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.x", PAGE_WIDTH);
        float y = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.y", PAGE_HEIGHT);

        if (validatedRequestJson.has("deliveryTimeRange")) {
            x += 90;
        }

        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(2);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + 10, y + 25);
        contentStream.lineTo(x + 75, y + 25);
        contentStream.lineTo(x + 85, y);
        contentStream.lineTo(x + 75, y - 25);
        contentStream.lineTo(x + 10, y - 25);
        contentStream.lineTo(x, y);
        contentStream.closePath();
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x + 15, y);
        contentStream.showText("Persönliche");
        contentStream.endText();
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x + 19, y - 10f);
        contentStream.showText("Übergabe");
        contentStream.endText();
        contentStream.stroke();
    }

    private void drawDeliveryDate(PDPageContentStream contentStream) throws IOException {
        float x = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.x", PAGE_WIDTH);
        float y = Utils.getCalculatedProperty(getDocumentLayoutProperties(), "hexagon.y", PAGE_HEIGHT);

        if (validatedRequestJson.has("namedPersonOnly") && (boolean) validatedRequestJson.get("namedPersonOnly")) {
            x += 90;
        }

        if (validatedRequestJson.has("deliveryTimeRange")) {
            x += 90;
        }

        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(2);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + 10, y + 25);
        contentStream.lineTo(x + 75, y + 25);
        contentStream.lineTo(x + 85, y);
        contentStream.lineTo(x + 75, y - 25);
        contentStream.lineTo(x + 10, y - 25);
        contentStream.lineTo(x, y);
        contentStream.closePath();
        contentStream.stroke();

        contentStream.stroke();
        contentStream.setStrokingColor(java.awt.Color.BLACK);
        contentStream.setNonStrokingColor(java.awt.Color.WHITE);
        contentStream.setLineWidth(1);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + 85, y);
        contentStream.closePath();
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x + 16, y + 9);
        contentStream.showText("Wunschtag");
        contentStream.endText();
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(java.awt.Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x + 19.5f, y - 18f);
        contentStream.showText(validatedRequestJson.getString("deliveryDate"));
        contentStream.endText();
        contentStream.stroke();
    }

    @Override
    public void validateRequest(AddressLabel addressLabel) {
        loadClientConfigurations(addressLabel.getFreightForwarder().getKey(), addressLabel.getClient());

        validateClient(addressLabel);
        validatePrintSize(addressLabel);
        Utils.validateName(addressLabel, "sender", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation(),
            false);
        Utils.validateName(addressLabel, "recipient", getStringKeys(), getIntegerKeys(),
            getExcludeFieldsFromValidation(), false);

        initializeShipmentInformation(addressLabel);
        initializeRoutingCode(addressLabel);
        initializeTrackingCode(addressLabel);

        for (String key : getStringKeys()) {
            Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), key, getStringKeys(), getIntegerKeys(),
                getExcludeFieldsFromValidation());
        }

        validateDeliveryDateTimeProperties(addressLabel);
        validateNamedPersonOnly(addressLabel);
        Utils.validateLabelSettings(addressLabel, getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
    }

    private void validateClient(AddressLabel addressLabel) {
        if (!clientSpecificExcludes.containsKey(addressLabel.getClient())) {
            throw new ALGException(10030, "Client Not supported.");
        }

        List<String> list = clientSpecificExcludes.get(addressLabel.getClient());
        if (!CollectionUtils.isEmpty(list)) {
            for (String fieldName : list) {
                String[] keys = fieldName.split("\\.");
                JSONObject requestJson = addressLabel.getRequestJson();
                String key = null;
                for (int i = 0; i < keys.length - 1; i++) {
                    key = keys[i];
                    if (requestJson.has(key)) {
                        requestJson = requestJson.getJSONObject(key);
                    } else {
                        break;
                    }
                }

                key = keys[keys.length - 1];

                if (requestJson.has(key)) {
                    LOG.debug("Removed {} key from requestJSON.", fieldName);
                    requestJson.remove(key);
                }
            }
        }
    }

    private void validateDeliveryDateTimeProperties(AddressLabel addressLabel) {
        if (!addressLabel.getRequestJson().has("deliveryTimeRange")) {
            String deliveryTimeRangeIdentifier = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                "freightForwarder.customProperties.deliveryTimeRangeIdentifier", getStringKeys(), getIntegerKeys(),
                getExcludeFieldsFromValidation());

            if (!StringUtils.isBlank(deliveryTimeRangeIdentifier)) {
                DhlTimerangeCodes timerangeCodes = DomainRegistryService.instance().dhlTimerangeCodesRepository()
                    .findByPk(deliveryTimeRangeIdentifier);
                if (timerangeCodes == null) {
                    throw new ALGException(10028,
                        "Field 'deliveryTimeRangeIdentifier' contains an invalid entry. Please provide a valid entry");
                } else {
                    addressLabel.getRequestJson().put("deliveryTimeRange", timerangeCodes.getRange());
                }
            }
        }

        String deliveryDate = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(),
            "freightForwarder.customProperties.deliveryDate", getStringKeys(), getIntegerKeys(),
            getExcludeFieldsFromValidation());
        if (!StringUtils.isBlank(deliveryDate)) {
            try {
                SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTo = new SimpleDateFormat("dd.MM.yyyy");
                sdfFrom.setLenient(false);
                sdfTo.setLenient(false);
                deliveryDate = sdfTo.format(sdfFrom.parse(deliveryDate));
                addressLabel.getRequestJson().put("deliveryDate", deliveryDate);
            } catch (Exception ex) {
                LOG.trace(ex.getMessage(), ex);
                throw new ALGException(10028,
                    "Field 'deliveryDate' contains an invalid entry. Please provide a valid entry", ex);
            }
        }
    }

    private void validatePrintSize(AddressLabel addressLabel) {
        String printSize = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), "printSize", getStringKeys(),
            getIntegerKeys(), getExcludeFieldsFromValidation());
        String product = Utils.getValueOfValidatedJSONKey(addressLabel.getRequestJson(), "freightForwarder.product",
            getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
        if (!clientConfigurationsJson.getString("product").equalsIgnoreCase(product)
            || !clientConfigurationsJson.getString("printSize").equalsIgnoreCase(printSize)) {
            throw new ALGException(10029,
                "Supported label for this client is:: " + clientConfigurationsJson.getString("product") + " of size "
                    + clientConfigurationsJson.getString("printSize"));
        }

        LOG.debug("PrintSize = {}", printSize);
    }

    private void initializeShipmentInformation(AddressLabel addressLabel) {
        String billingNo = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
            "freightForwarder.customProperties.billingNo", getStringKeys(), getIntegerKeys(),
            getExcludeFieldsFromValidation());

        if (StringUtils.isBlank(billingNo)) {
            if (addressLabel.getRequestJson().has("freightForwarder")
                && !addressLabel.getRequestJson().getJSONObject("freightForwarder").has("customProperties")) {
                addressLabel.getRequestJson().getJSONObject("freightForwarder").put("customProperties",
                    new JSONObject());
            }

            addressLabel.getRequestJson().getJSONObject("freightForwarder").getJSONObject("customProperties")
                .put("billingNo", clientConfigurationsJson.getString("billingNo"));
        }
    }

    private void initializeRoutingCode(AddressLabel addressLabel) {
        if (!addressLabel.getRequestJson().has("routingcode")
            || StringUtils.isBlank(addressLabel.getRequestJson().getString("routingcode"))) {

            String routingCodeType = clientConfigurationsJson.getString("routingCodeType");
            JSONObject routingCodeData = new JSONObject();

            switch (routingCodeType) {
                case "STANDARD": {
                    JSONObject recipientAddressJSONObject = addressLabel.getRequestJson().getJSONObject("recipient");

                    for (String key : recipientAddressJSONObject.keySet()) {
                        routingCodeData.put(key, recipientAddressJSONObject.get(key));
                    }
                    break;
                }

                case "GS1": {
                    String country = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "recipient.country", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
                    if (StringUtils.isBlank(country)) {
                        throw new ALGException(10011, "Field 'country' is blank. Please provide a valid 'country'.");
                    }
                    routingCodeData.put("country", country);
                    routingCodeData.put("postalCode",
                        Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(), "recipient.postalCode",
                            getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
                    routingCodeData.put("street", Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "recipient.street", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
                    routingCodeData.put("city", Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "recipient.city", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));

                    String houseNo = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "recipient.houseNo", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
                    if (!StringUtils.isBlank(houseNo)) {
                        routingCodeData.put("houseNo", houseNo);
                    }

                    String deliveryTimeRangeIdentifier = Utils.getStringValueOfValidatedJSONKey(
                        addressLabel.getRequestJson(), "freightForwarder.customProperties.deliveryTimeRangeIdentifier",
                        getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation());
                    if (!StringUtils.isBlank(deliveryTimeRangeIdentifier)) {
                        routingCodeData.put("deliveryTimeRangeIdentifier", deliveryTimeRangeIdentifier);
                    }

                    String deliveryDate = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "freightForwarder.customProperties.deliveryDate", getStringKeys(), getIntegerKeys(),
                        getExcludeFieldsFromValidation());
                    String namedPersonOnly = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "freightForwarder.customProperties.namedPersonOnly", getStringKeys(), getIntegerKeys(),
                        getExcludeFieldsFromValidation());
                    if (!StringUtils.isBlank(deliveryDate) || !StringUtils.isBlank(deliveryTimeRangeIdentifier)
                        || !StringUtils.isBlank(namedPersonOnly)) {
                        routingCodeData.put("productcode", "43");
                    } else {
                        routingCodeData.put("productcode", "00");
                    }

                    break;
                }
            }

            LOG.debug("routingCodeData={}", routingCodeData);

            String routingCode = getRoutingCodeService()
                .getRoutingCodeFor(clientConfigurationsJson.getString("routingCodeType"), routingCodeData);
            addressLabel.getRequestJson().put("routingCode", routingCode);
        }
    }

    private void initializeTrackingCode(AddressLabel addressLabel) {
        if (!addressLabel.getRequestJson().has("trackingCode")
            || StringUtils.isBlank(addressLabel.getRequestJson().getString("trackingCode"))) {
            String trackingCodeType = clientConfigurationsJson.getString("trackingCodeType");

            JSONObject trackingCodeData = new JSONObject();
            trackingCodeData.put("trackingCodeType", trackingCodeType);
            trackingCodeData.put("client", addressLabel.getClient());
            switch (trackingCodeType) {
                case "PPU": {
                    trackingCodeData.put("weightInIntegerRepresentation",
                        Utils.getIntegerValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                            "weight.weightInIntegerRepresentation", getStringKeys(), getIntegerKeys(),
                            getExcludeFieldsFromValidation()));
                    trackingCodeData.put("unit", Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                        "weight.unit", getStringKeys(), getIntegerKeys(), getExcludeFieldsFromValidation()));
                    if (clientConfigurationsJson.has("ppu.username") && clientConfigurationsJson.has("ppu.password")) {
                        trackingCodeData.put("ppuUserName", clientConfigurationsJson.get("ppu.username"));
                        trackingCodeData.put("ppuPassword", clientConfigurationsJson.get("ppu.password"));
                    } else {
                        trackingCodeData.put("ppuUserName",
                            Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                                "freightForwarder.customProperties.ppuUserName", getStringKeys(), getIntegerKeys(),
                                getExcludeFieldsFromValidation()));
                        trackingCodeData.put("ppuPassword",
                            Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
                                "freightForwarder.customProperties.ppuPassword", getStringKeys(), getIntegerKeys(),
                                getExcludeFieldsFromValidation()));
                    }

                    break;
                }

                case "NUMBER_RANGE": {
                    break;
                }

                default: {
                    throw new ALGException(10033,
                        "Tracking code generation strategy (" + trackingCodeType + ") not supported.");
                }
            }

            LOG.debug("trackingCodeData={}", trackingCodeData);

            String trackingCode = getTrackingCodeService().getTrackingCode(trackingCodeData);

            addressLabel.getRequestJson().put("trackingCode", trackingCode);
            addressLabel.setTrackingCode(trackingCode);
        }
    }

    private void validateNamedPersonOnly(AddressLabel addressLabel) {
        String namedPersonOnly = Utils.getStringValueOfValidatedJSONKey(addressLabel.getRequestJson(),
            "freightForwarder.customProperties.namedPersonOnly", getStringKeys(), getIntegerKeys(),
            getExcludeFieldsFromValidation());
        if (StringUtils.isBlank(namedPersonOnly)) {
            addressLabel.getRequestJson().put("namedPersonOnly", false);
        } else {
            if (namedPersonOnly.equalsIgnoreCase("true") || namedPersonOnly.equalsIgnoreCase("false")) {
                addressLabel.getRequestJson().put("namedPersonOnly", Boolean.valueOf(namedPersonOnly));
            } else {
                throw new ALGException(10032,
                    "Field 'namedPersonOnly' contains an invalid entry. Please provide a valid entry");
            }
        }
    }
}

package com.brodos.reservation.component;

import com.brodos.reservation.dto.request.AddressType;
import com.brodos.reservation.dto.request.PurchaseType;
import com.brodos.reservation.dto.request.Salutation;
import static com.brodos.reservation.dto.request.Salutation.Frau;
import com.brodos.reservation.entity.Customer;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.events.RequestedForSendout;
import java.time.LocalDate;

import com.brodos.xmlserver.voucher.entity.Address;
import com.brodos.xmlserver.voucher.entity.Country;
import com.brodos.xmlserver.voucher.entity.CustomerNo;
import com.brodos.xmlserver.voucher.entity.Data;
import com.brodos.xmlserver.voucher.entity.HandlingMode;
import com.brodos.xmlserver.voucher.entity.Header;
import com.brodos.xmlserver.voucher.entity.Position;
import com.brodos.xmlserver.voucher.entity.Positions;
import com.brodos.xmlserver.voucher.entity.Serials;
import com.brodos.xmlserver.voucher.entity.StockTo;
import com.brodos.xmlserver.voucher.entity.Unit;
import com.brodos.xmlserver.voucher.entity.Voucher;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final String APPNAME = "IMEI Reservierung";
    private final int ONE = 1;
    private final int ZERO = 0;
    private final String WAREHOUSE = "2";
    private final int HANDLINGMODE = 35;
    private final String ADDRESSTYPE = "DELIVER";
    private final String COUNTRYCODE = "DEU";
    private final String UNITKEY = "ST.";

    public Data map(RequestedForSendout requestedForSendout) {
        Data data = new Data();
        data.setOrigin(APPNAME);
        data.setMessageId("TICKET" + requestedForSendout.getTicketReference().getTicketNumber() + "ID"
            + String.valueOf(requestedForSendout.getTicketReference().getId()));
        data.setSystemId(String.valueOf(ONE));
        data.setVoucher(initializeVoucher(requestedForSendout));
        return data;
    }

    private Voucher initializeVoucher(RequestedForSendout requestedForSendout) {
        Voucher voucher = new Voucher();
        voucher.setHeader(initializeHeader(requestedForSendout));
        Positions positions = initializePosition(requestedForSendout);
        voucher.setPositions(positions);
        return voucher;
    }

    private Header initializeHeader(RequestedForSendout requestedForSendout) {
        SerialNumberReservationTicketReference serialNumberReservationTicketReference =
            (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
        Customer customer = (Customer) serialNumberReservationTicketReference.getSerialNumberReservation().getOwner();
        Header header = new Header();
        header.setVoucherType("K");
        header.setVoucherGroup("HW");
        header.setReference("IMEI Res T-" + serialNumberReservationTicketReference.getTicketNumber());
        header.setCCVoucherRefNo("IMEI Res T-" + serialNumberReservationTicketReference.getTicketNumber());

        header.setVoucherDate(String.valueOf(LocalDate.now()));
        header.setValutaDate(String.valueOf(LocalDate.now()));
        header.setVatRated(String.valueOf(ONE));
        StockTo stockTo = new StockTo();
        stockTo.setKey(WAREHOUSE);
        stockTo.setValue(WAREHOUSE);
        if (requestedForSendout.getGroup() != 0 || requestedForSendout.getGroup() != null) {
            stockTo.setKey(String.valueOf(requestedForSendout.getGroup()));
            stockTo.setValue(String.valueOf(requestedForSendout.getGroup()));
        }
        stockTo.setKey(String.valueOf(requestedForSendout.getGroup()));
        stockTo.setValue(String.valueOf(requestedForSendout.getGroup()));
        header.setStockTo(stockTo);

        CustomerNo customerNo = new CustomerNo();
        customerNo.setValue(customer.getCustomerNumber());
        header.setCustomerNo(customerNo);

        header.setGeneratePdf(String.valueOf(ZERO));
        header.setIsFibu(String.valueOf(ONE));

        JsonNode deliveryDetails = requestedForSendout.getDeliveryDetails();
        if (deliveryDetails != null && deliveryDetails.has("delivery_address")) {
            header.getAddress().add(initializeDeliveryAddress(deliveryDetails.get("delivery_address")));
        }
        header.setVoucherStatus(String.valueOf(ZERO));
        if (deliveryDetails != null
            && deliveryDetails.get("shipt_to").asText().equalsIgnoreCase(AddressType.CUSTOMER.name())) {
            HandlingMode handlingMode = new HandlingMode();
            handlingMode.setNo(String.valueOf(HANDLINGMODE)); // When strece is selected
            handlingMode.setContent(APPNAME);
            header.setHandlingMode(handlingMode);
        }
        header.setAutoAccept(String.valueOf(ZERO));
        return header;
    }

    private Positions initializePosition(RequestedForSendout requestedForSendout) {
        SerialNumberReservationTicketReference serialNumberReservationTicketReference =
            (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
        Positions positions = new Positions();
        Position position = new Position();
        position.setPosNo(String.valueOf(ONE));
        position.setPosCode1("A");
        JsonNode deliveryDetails = requestedForSendout.getDeliveryDetails();
        if (deliveryDetails.get("purchase_type").asText().equals(PurchaseType.COMMISSION.name())) {
            position.setPosCode2("K");
            position.setPrice("GK");
        }
        position.setArticleNo(serialNumberReservationTicketReference.getSerialNumberReservation().getSerialNumber()
            .getArticle().getArticleId().getArticleNumber());
        position.setOrdered(String.valueOf(ONE));
        position.setDelivered(String.valueOf(ONE));
        position.setBacklog(String.valueOf(ZERO));

        Unit unit = new Unit();
        unit.setKey(UNITKEY);
        unit.setValue("St.");
        position.setUnit(unit);
        position.setVatIncluded(String.valueOf(ZERO));
        Serials serials = new Serials();
        serials.getSerialNo().add(
            serialNumberReservationTicketReference.getSerialNumberReservation().getSerialNumber().getNumber());
        position.setSerials(serials);
        position.setStockKey(WAREHOUSE);
        if (requestedForSendout.getGroup() != 0 || requestedForSendout.getGroup() != null) {
            position.setStockKey(String.valueOf(requestedForSendout.getGroup()));
        }
        positions.getPosition().add(position);
        return positions;
    }

    private Address initializeDeliveryAddress(JsonNode addressNode) {
        Address address = new Address();
        address.setAddressType(ADDRESSTYPE);
        if (addressNode.has("city")) {
            address.setCity(addressNode.get("city").asText());
        }
        Country country = new Country();
        country.setValue(COUNTRYCODE);
        address.setCountry(country);
        address.setHouseNo(addressNode.get("houseno").asText());
        if (addressNode.has("firstname")) {
            address.setName1(addressNode.get("firstname").asText());
        }
        if (addressNode.has("lastname")) {
            address.setName2(addressNode.get("lastname").asText());
        }
        if (addressNode.has("salutation")) {
            switch (Salutation.valueOf(addressNode.get("salutation").asText())) {
                case Frau: {
                    address.setSalutation("F");
                    break;
                }

                case Firma: {
                    address.setSalutation("FI");
                    address.setName1(addressNode.get("company").asText());
                    break;
                }

                case Herr: {
                    address.setSalutation("H");
                    break;
                }
            }
        }
        if (addressNode.has("street")) {
            address.setStreet(addressNode.get("street").asText());
        }
        address.setZipCode(addressNode.get("zipcode").asText());
        return address;
    }
}

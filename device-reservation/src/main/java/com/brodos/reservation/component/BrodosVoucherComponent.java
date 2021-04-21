/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import com.brodos.config.dataobject.ConfigData;
import com.brodos.xmlserver.voucher.persistence.repository.impl.VoucherRepositoryImpl;
import com.brodos.xmlserver.voucher.persistence.service.impl.ConfigServiceImpl;
import com.brodos.xmlserver.voucher.persistence.service.impl.ImportVoucherGatewayImpl;
import com.brodos.xmlserver.voucher.service.impl.VoucherDtoMapperImpl;
import com.brodos.xmlserver.voucher.service.impl.VoucherServiceImpl;
import com.brodos.xmlserverclient.XmlServerClient;
import com.brodos.xmlservervoucher.XmlServerVoucher;
import com.brodos.xmlserver.voucher.service.VoucherService;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//To test voucher creation using xml server
//import com.brodos.xmlserver.voucher.entity.Address;
//import com.brodos.xmlserver.voucher.entity.Country;
//import com.brodos.xmlserver.voucher.entity.CustomerNo;
//import com.brodos.xmlserver.voucher.entity.Data;
//import com.brodos.xmlserver.voucher.entity.HandlingMode;
//import com.brodos.xmlserver.voucher.entity.Header;
//import com.brodos.xmlserver.voucher.entity.Position;
//import com.brodos.xmlserver.voucher.entity.Positions;
//import com.brodos.xmlserver.voucher.entity.Serials;
//import com.brodos.xmlserver.voucher.entity.StockTo;
//import com.brodos.xmlserver.voucher.entity.Unit;
//import com.brodos.xmlserver.voucher.entity.Voucher;
//import com.brodos.xmlserver.voucher.entity.VoucherReturnContainer;

/**
 *
 * @author padhaval
 */

@Component
@PropertySource("classpath:brodos.xmlserverconnector.properties")
public class BrodosVoucherComponent {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BrodosVoucherComponent.class);

    @Autowired
    private Environment brodosXmlServerConnectorEnvironment;

    private VoucherService voucherService;

    @PostConstruct
    public void postConstruct() {
        ConfigData configData = new ConfigData();
        configData.setHost(brodosXmlServerConnectorEnvironment.getProperty("host"));
        configData.setPort(Integer.valueOf(brodosXmlServerConnectorEnvironment.getProperty("port")));
        configData.setPortStr(brodosXmlServerConnectorEnvironment.getProperty("port"));
        configData.setTimeout(Integer.valueOf(brodosXmlServerConnectorEnvironment.getProperty("timeout")));
        configData.setTimeoutStr(brodosXmlServerConnectorEnvironment.getProperty("timeout"));
        configData.setModuleid(brodosXmlServerConnectorEnvironment.getProperty("vouchermoduleid"));

        LOG.info("XML Server Connector configData={}", configData);

        ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
        configServiceImpl.setConfigData(configData);

        XmlServerClient xmlServerClient = new XmlServerClient();
        XmlServerVoucher xmlServerVoucher = new XmlServerVoucher();
        xmlServerVoucher.setClient(xmlServerClient);

        ImportVoucherGatewayImpl importVoucherGatewayImpl = new ImportVoucherGatewayImpl();
        importVoucherGatewayImpl.setConfigurationService(configServiceImpl);
        importVoucherGatewayImpl.setXmlServerVoucher(xmlServerVoucher);

        VoucherRepositoryImpl voucherRepositoryImpl = new VoucherRepositoryImpl();
        voucherRepositoryImpl.setImportVoucherService(importVoucherGatewayImpl);

        VoucherDtoMapperImpl voucherDtoMapperImpl = new VoucherDtoMapperImpl();

        VoucherServiceImpl voucherServiceImpl = new VoucherServiceImpl();
        voucherServiceImpl.setVoucherRepository(voucherRepositoryImpl);
        voucherServiceImpl.setMapper(voucherDtoMapperImpl);
        this.voucherService = voucherServiceImpl;

        // To test voucher creation using xml server
        // test();
    }

    public VoucherService getVoucherService() {
        return voucherService;
    }

    public String getUserName() {
        return brodosXmlServerConnectorEnvironment.getProperty("brodos.voucher.socket.username");
    }

    public String getPassword() {
        return brodosXmlServerConnectorEnvironment.getProperty("brodos.voucher.socket.password");
    }

    // Method To test voucher creation using xml server
    // private void test() {
    // Data data = new Data();
    // data.setMessageId("TICKET1366493ID5");
    // data.setSystemId("1");
    // data.setOrigin("IMEI Reservierung");
    // data.setVoucher(new Voucher());
    // data.getVoucher().setHeader(new Header());
    // data.getVoucher().setPositions(new Positions());
    //
    // data.getVoucher().getHeader().setVoucherType("K");
    // data.getVoucher().getHeader().setVoucherGroup("HW");
    // data.getVoucher().getHeader().setCCVoucherRefNo("IMEI Res T-1366493");
    // data.getVoucher().getHeader().setVoucherDate("2020-07-21");
    // data.getVoucher().getHeader().setValutaDate("2020-07-21");
    // data.getVoucher().getHeader().setVatRated("1");
    // data.getVoucher().getHeader().setCustomerNo(new CustomerNo("66666"));
    //
    // StockTo stockTo = new StockTo();
    // stockTo.setKey("2");
    // stockTo.setValue("2");
    // data.getVoucher().getHeader().setStockTo(stockTo);
    //
    // data.getVoucher().getHeader().setReference("IMEI Res T-1366493");
    // data.getVoucher().getHeader().setGeneratePdf("0");
    // data.getVoucher().getHeader().setIsFibu("1");
    // data.getVoucher().getHeader().setVoucherStatus("0");
    //
    // HandlingMode handlingMode = new HandlingMode();
    // handlingMode.setNo("35");
    // handlingMode.setContent("IMEI Reservierung");
    // data.getVoucher().getHeader().setHandlingMode(handlingMode);
    //
    // data.getVoucher().getHeader().setAutoAccept("0");
    //
    // Address address = new Address();
    // address.setAddressType("DELIVER");
    // address.setCity("Götz");
    // Country country = new Country();
    // country.setValue("DEU");
    // address.setCountry(country);
    // address.setHouseNo("45");
    // address.setName1("Ölschläger");
    // address.setName2("Mercatorstraße");
    // // address.setPostBox("1356");
    // address.setSalutation("F");
    // address.setStreet("Mercatorstraße");
    // address.setZipCode("1356");
    // address.setZipPostBox("1356");
    //
    // data.getVoucher().getHeader().getAddress().add(address);
    //
    // Position position = new Position();
    // position.setPosNo("1");
    // position.setPosCode1("A");
    // position.setArticleNo("SAG930F-GOO2OS");
    // position.setStockKey("2");
    // position.setOrdered("1");
    // position.setDelivered("1");
    // position.setBacklog("0");
    // Unit unit = new Unit();
    // unit.setKey("ST.");
    // unit.setValue("St.");
    // position.setUnit(unit);
    // position.setVatIncluded("0");
    //
    // position.setSerials(new Serials());
    // position.getSerials().getSerialNo().add("dhavaltest311");
    //
    // Positions positions = new Positions();
    // positions.getPosition().add(position);
    //
    // data.getVoucher().setPositions(positions);
    //
    // VoucherReturnContainer createVoucher = getVoucherService().createVoucher(data, "superadmin", "123456");
    // System.out.println(createVoucher);
    // }
}

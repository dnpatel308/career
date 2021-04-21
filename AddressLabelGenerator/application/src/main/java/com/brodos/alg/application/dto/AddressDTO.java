/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author padhaval
 */
@JsonPropertyOrder({"company", "name1", "name2", "name3", "street", "country", "isoCountryCode", "postalCode", "city", "houseNo", "phoneNo", "email"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    private String company;
    private String name1;
    private String name2;
    private String name3;
    @NotBlank(message = "Field 'street' is blank. Please provide a valid 'street'")
    private String street;
    private String country;
    @NotBlank(message = "Field 'country code' is blank. Please provide a valid 'country code'")
    private String isoCountryCode;
    @NotBlank(message = "Field 'postal code' is blank. Please provide a valid 'postal code'")
    private String postalCode;
    @NotBlank(message = "Field 'city' is blank. Please provide a valid 'city'")
    private String city;
//    @NotBlank(message = "Field 'house number' is blank. Please provide a valid 'house number'")
    private String houseNo;
    private String phoneNo;
    private String email;

    public AddressDTO() {

    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public void setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

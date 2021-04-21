/**
 * 
 */
package com.brodos.device.reservation.test.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author snihit
 *
 * 
 */
public class RequestSentoutAddressDTO {

    @JsonProperty("company")
    String companyName;
    @JsonProperty("firstname")
    String firstName;
    @JsonProperty("lastname")
    String lastName;

    String salutation;
    @JsonProperty("suffix")
    String nameAffix;

    String city;
    String street;
    @JsonProperty("houseno")
    String houseNo;
    @JsonProperty("zipcode")
    String zipCode;
    String state;
    String country;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getNameAffix() {
        return nameAffix;
    }

    public void setNameAffix(String nameAffix) {
        this.nameAffix = nameAffix;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public void setCountry(String country)
    {
    	this.country = country ;
    }
    
    public String getCountry() {
    	return country ;
    }
    
    public void setState(String state)
    {
    	this.state = state ;
    }
    
    public String getState() {
    	return state ;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
package com.brodos.reservation.dto.request;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.exception.DeviceReservationException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.brodos.reservation.validator.Enum;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class RequestSentoutAddressDTO {

    @JsonProperty("company")
    String companyName;
    @JsonProperty("firstname")
    String firstName;
    @JsonProperty("lastname")
    String lastName;

    @Enum(enumClass = Salutation.class, message = "Please provide valid salutation.")
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

    public void validate(String addressType) {
        validateHouseNo();
        validateStreet();
        validateCity();
        validateZipcode();
        validateSalutation();
        if (salutation.equals(Salutation.Firma.name())) {
            validateCompanyName();
        } else {
            validateFirstLastName();
        }
    }

    private void validateCompanyName() {
        if (StringUtils.isBlank(companyName)) {
            throw new DeviceReservationException(ErrorCodes.BLANK_CUSTOMER_AND_COMPANY_NAME);
        }
    }

    private void validateFirstLastName() {
        if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
            throw new DeviceReservationException(ErrorCodes.BLANK_FIRST_LAST_NAME);
        }
    }

    private void validateHouseNo() {
        if (StringUtils.isBlank(houseNo)) {
            throw new DeviceReservationException(ErrorCodes.INVALID_HOUSE_NO);
        }
    }

    private void validateStreet() {
        if (StringUtils.isBlank(street)) {
            throw new DeviceReservationException(ErrorCodes.INVALID_STREET);
        }
    }

    private void validateZipcode() {
        if (StringUtils.isBlank(zipCode)) {
            throw new DeviceReservationException(ErrorCodes.INVALID_ZIPCODE);
        }
    }

    private void validateSalutation() {
        if (StringUtils.isBlank(salutation)) {
            throw new DeviceReservationException(ErrorCodes.INVALID_SALUTATION);
        }
    }

    private void validateCity() {
        if (StringUtils.isBlank(city)) {
            throw new DeviceReservationException(ErrorCodes.BLANK_CITY);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

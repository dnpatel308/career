/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author padhaval
 */
public enum ErrorCodes {

    REQUEST_BODY_MISSING(400, "Required request body is missing."),
    DB_CONNECTION_ERROR(500, "Unable to connect database."),
    DEVICE_CONTEXT_ERROR(500, "Unable to fetch valid device context info."),
    RESERVATION_NOT_FOUND(404, "Device reservation not found."),
    BULK_RESERVATION_NOT_FOUND(404, "Bulk reservation not found."),
    ARTICLE_NOT_FOUND(404, "Article not found."),
    ARTICLE_NOT_SUPPORT_ISSERIAL(404, "Article is not supporting serial number."),
    STATE_CHANGE_NOT_ALLOWED(409, "State change currently not allowed"),
    CUSTOMER_NOT_FOUND(404, "Customer not found for given customerNumber."),
    INVALID_RESERVATION_STATUS_IN_REQUEST(400, "Invalid reservation status provided."),
    SERIAL_NO_MISSING_REQUEST(400, "Serial number missing."),
    DEVICE_ALREADY_RESERVERD(400, "Device already reserved for customer."),
    DEVICE_ALREADY_REQUESTEDFORSENTOUT(400, "Device already requested for sentout."),
    DEVICE_ALREADY_SENTOUT(400, "Device already sentout to customer."),
    DEVICE_ALREADY_AVAILABLE_INPOOL(400, "Device already available in pool."),
    DEVICE_ALREADY_AVAILABLE_INPOOL_WITH_DIFF_ARTICLE(400, "Device already available in pool with different article."),
    ARTICLE_API_ERROR(500, "Unable to fetch article detail."),
    INVALID_ACTION_REQUESTED(400, "Invalid action requested."),
    INTERNAL_SERVER_ERROR(500, "Internal server error."),
    SERIAL_NO_RELOCATED(400, "Serial number already relocated to requested warehouse."),
    CANCELLATION_REASON_MISSING(400, "Cancellation reason is mondatory."),
    INVALID_TO_WAREHOUSE(400, "To warehouse missing."),
    TEMPLATE_EXCEPTION(500, "Template exception, ParameterMap does not match with defined template"),
    INVALID_PURCHASE_TYPE(400, "Please provide valid purchase type."),
    INVALID_STREET(400, "Please provide street details."),
    INVALID_HOUSE_NO(400, "Please provide valid houseNo."),
    INVALID_ZIPCODE(400, "Please provide valid zipcode."),
    BLANK_CUSTOMER_AND_COMPANY_NAME(400, "Please provide valid CompanyName."),
    INVALID_ADDRESS_TYPE(400, "Invalid AddressType"),
    ADDRESS_TYPE_REQUIRED(400, "Please provide addressType."),
    IMEI_SENTOUT(400, "IMEI is already Sentout."),
    INVALID_SALUTATION(400, "Please provide valid salutation."),
    BLANK_FIRST_LAST_NAME(400, "Please provide valid FirstName and LastName."),
    BLANK_CITY(400, "Please provide valid city."),
    UNAUTHORISED_CUSTOMERNO_REQUESTED(400, "Invalid/UnAuthorised CustomerNumber."),
    INVAILD_SALESPERSONNEL_EMAIL(400, "Invalid SalePersonnel emailId provided."),
    TICKET_REFERENCE_NOT_FOUND(400, "TicketReference not found."),
    MISSING_ADDRESS(400, "Missing address."),
    MISSING_ARGUMENTS(400, "Missing arguments."),
    SERIAL_NO_FAILED_TO_PROCESS(400, "%d/%d serial numbers failed to process."),
    DEVICE_NOT_FOUND(404, "Device not found for given %s."),
    DEVICE_ALREADY_EXIST(409, "Device already exist."),
    DEVICE_NOT_ASSOCIATED_WITH_RESERVATION(400, "Device not associated with reservation.");

    private final int code;
    private final String message;
    private final Map<String, Object> metadata;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
        this.metadata = new HashMap<>();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public ErrorCodes putMetadata(String key, Object object) {
        metadata.put(key, object);
        return this;
    }
}

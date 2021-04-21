/**
 * 
 */
package com.brodos.device.reservation.test.dto;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author snihit
 *
 * 
 */
public class ProductCodeDTO {

	    @Pattern(regexp = "EAN", message = "Invalid product code type")
	    private String type;
	    private String value;

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public String getValue() {
	        return value;
	    }

	    public void setValue(String value) {
	        this.value = value;
	    }

	    @Override
	    public String toString() {
	        return ToStringBuilder.reflectionToString(this);
	    }
}

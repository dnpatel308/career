/**
 * 
 */
package com.brodos.device.reservation.test.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author snihit
 *
 * 
 */
public class DeviceConfigFieldDTO {
	
	  @NotBlank(message = "Field configuration name is mandatory")
	    private String name;
	    private String type;

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    @Override
	    public int hashCode() {
	        int hash = 7;
	        return hash;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	            return true;
	        }
	        if (obj == null) {
	            return false;
	        }
	        if (getClass() != obj.getClass()) {
	            return false;
	        }
	        final DeviceConfigFieldDTO other = (DeviceConfigFieldDTO) obj;
	        if (this.name != other.name) {
	            return false;
	        }
	        return true;
	    }

	    @Override
	    public String toString() {
	        return ToStringBuilder.reflectionToString(this);
	    }

}

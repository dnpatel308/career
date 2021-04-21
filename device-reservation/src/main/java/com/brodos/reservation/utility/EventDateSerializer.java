/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.utility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author padhaval
 */
public class EventDateSerializer extends StdSerializer<Date> {

    EventDateSerializer() {
        this(null);
    }

    public EventDateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date t, JsonGenerator jg, SerializerProvider sp) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String formattedDateString = sdf.format(t);
        formattedDateString = formattedDateString.replace("Z", "+00:00");
        jg.writeString(formattedDateString);
    }
}

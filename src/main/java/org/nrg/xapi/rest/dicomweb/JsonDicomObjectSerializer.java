package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.pgelinas.jackson.javax.json.stream.JacksonGenerator;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.json.JSONWriter;

public class JsonDicomObjectSerializer extends StdSerializer<Attributes> {

    JsonDicomObjectSerializer() {
        super(Attributes.class);
    }

    @Override
    public void serialize(Attributes value, JsonGenerator gen, SerializerProvider provider) {

        JacksonGenerator jgen = new JacksonGenerator( gen);
        JSONWriter jsonWriter = new JSONWriter(jgen);
        jsonWriter.write( value);
        jgen.flush();
    }

}

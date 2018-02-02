package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class TestJsonDicomResponseWriter {

    DicomWebSearchClient client = new DicomWebSearchClient("http://xnat-latest/xapi/dicomweb");
    JsonGenerator jsonGenerator;

    @Test
    public void testValidString() {

        Attributes attributes = new Attributes();

        attributes.setString( Tag.PatientSex, ElementDictionary.vrOf(Tag.PatientSex,null), "M");

        JsonDicomObjectSerializer serializer = new JsonDicomObjectSerializer();

        ByteArrayOutputStream os = new ByteArrayOutputStream( 1024);
        JsonGenerator gen = null;
        try {
            gen = new JsonFactory().createGenerator( os);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }

//        serializer.serialize( attributes, JsonGenerator gen, SerializerProvider provider);
        serializer.serialize( attributes, gen, null);

        assertEquals("{\"00100040\":{\"vr\":\"CS\",\"Value\":[\"M\"]}}", os.toString());
    }

    @Test
    public void testEmptyString() {

        Attributes attributes = new Attributes();

        attributes.setString(Tag.PatientSex, ElementDictionary.vrOf(Tag.PatientSex, null), "");

        JsonDicomObjectSerializer serializer = new JsonDicomObjectSerializer();

        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        JsonGenerator gen = null;
        try {
            gen = new JsonFactory().createGenerator(os);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }

        serializer.serialize( attributes, gen, null);

        assertEquals("{\"00100040\":{\"vr\":\"CS\"}}", os.toString());
    }

    @Test
    public void testNullString() {

        Attributes attributes = new Attributes();

        String value = null;
        attributes.setString( Tag.PatientSex, ElementDictionary.vrOf(Tag.PatientSex,null), value);

        JsonDicomObjectSerializer serializer = new JsonDicomObjectSerializer();

        ByteArrayOutputStream os = new ByteArrayOutputStream( 1024);
        JsonGenerator gen = null;
        try {
            gen = new JsonFactory().createGenerator( os);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }

        serializer.serialize( attributes, gen, null);

        assertEquals("{\"00100040\":{\"vr\":\"CS\"}}", os.toString());
    }

}



package org.nrg.xapi.model.dicomweb;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.io.SAXWriter;
import org.nrg.framework.annotations.XnatMixIn;
import org.nrg.xapi.rest.dicomweb.QIDOResponseMixin;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidmaffitt on 8/3/17.
 */
@XmlRootElement
@XnatMixIn(QIDOResponseMixin.class)
public class QIDOResponse extends Attributes {

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"


    protected SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd");
    protected SimpleDateFormat timeFormat = new SimpleDateFormat( "HHmmss");


    public static JsonGenerator createGenerator(OutputStream out) {
        boolean indent = true;
        Map<String, ?> conf = new HashMap<String, Object>(2);
        if (indent)
            conf.put(JsonGenerator.PRETTY_PRINTING, null);
        return Json.createGeneratorFactory(conf).createGenerator(out);
    }

//    public static void main(String[] args) {
//        QIDOResponse response = new QIDOResponse();
//        response.setStudyDate("20170519");
//
//        JsonGenerator jsonGen = createGenerator(System.out);
//        JSONWriter jsonWriter = new JSONWriter(jsonGen);
//        jsonWriter.write(response);
//        jsonGen.flush();
//    }

    private static TransformerHandler getTransformerHandler()
            throws TransformerConfigurationException, IOException {
        SAXTransformerFactory tf = (SAXTransformerFactory)
                TransformerFactory.newInstance();
        String xsltURL = null;
        if (xsltURL == null)
            return tf.newTransformerHandler();

        TransformerHandler th = tf.newTransformerHandler(
                new StreamSource(xsltURL));
        return th;
    }

    public static void main(String[] args) {
        QIDOResponse response = new QIDOResponse();
//        response.setStudyDate("20170519");

        TransformerHandler th = null;
        try {
            th = QIDOResponse.getTransformerHandler();

            SAXWriter writer = new SAXWriter( th);
            Transformer t = th.getTransformer();
//            t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
//            t.setOutputProperty(OutputKeys.VERSION, xmlVersion);
            th.setResult(new StreamResult(System.out));
            writer.write( response);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}

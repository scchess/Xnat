package org.nrg.xapi.rest.dicomweb;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.SAXWriter;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.charset.Charset;

public class Dicom2XmlMessageConverter extends AbstractHttpMessageConverter<Attributes> {

    private TransformerHandler transformerHandler = null;

    public Dicom2XmlMessageConverter() {
        super(new MediaType("application","dicom+xml"));
    }

    @Override
    protected Attributes readInternal(Class<? extends Attributes> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Attributes.class.isAssignableFrom( clazz);
    }

    @Override
    protected void writeInternal(Attributes dicomObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        TransformerHandler th;
        try {
            th = getTransformerHandler();

            SAXWriter writer = new SAXWriter( th);
//            Transformer t = th.getTransformer();
//            t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
//            t.setOutputProperty(OutputKeys.VERSION, xmlVersion);
            th.setResult(new StreamResult( httpOutputMessage.getBody()));
            writer.write( dicomObject);

        } catch (TransformerConfigurationException | SAXException e) {
            throw new HttpMessageNotWritableException( "", e);
        }
    }

    /**
     * Create the TransformerHandler lazily.
     *
     * @return
     * @throws TransformerConfigurationException
     * @throws IOException
     */
    private TransformerHandler getTransformerHandler() throws TransformerConfigurationException, IOException {
        if( transformerHandler == null) {
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
            String xsltURL = null;
            if (xsltURL == null)
                return tf.newTransformerHandler();

            TransformerHandler transformerHandler = tf.newTransformerHandler( new StreamSource(xsltURL));
        }
        return transformerHandler;
    }
}

package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;

public class DicomObjectMessageConverter extends AbstractHttpMessageConverter<DicomObjectI> {

    private TransformerHandler transformerHandler = null;

    public DicomObjectMessageConverter() {
        super(new MediaType("application","dicom+xml"));
    }

    @Override
    protected DicomObjectI readInternal(Class<? extends DicomObjectI> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DicomObjectI.class.isAssignableFrom( clazz);
    }

    @Override
    protected void writeInternal(DicomObjectI dicomObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        dicomObject.write( httpOutputMessage.getBody());
    }

}

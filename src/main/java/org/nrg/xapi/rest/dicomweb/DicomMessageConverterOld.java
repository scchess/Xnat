package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by davidmaffitt on 7/12/17.
 */
public class DicomMessageConverterOld extends AbstractHttpMessageConverter<DicomObjectI> {

    public DicomMessageConverterOld() {
        super(new MediaType("application","dicom"));
    }

    @Override
    protected DicomObjectI readInternal(Class<? extends DicomObjectI> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        DicomObjectI dicomObject = DicomObjectFactory.create( arg1.getBody());
        return dicomObject;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DicomObjectI.class.isAssignableFrom( clazz);
//        return true;
    }

    @Override
    protected void writeInternal(DicomObjectI dicomObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
//        dicomObject.write( httpOutputMessage.getBody());

        httpOutputMessage.getBody().write("DICOM object here: ".getBytes(Charset.forName("UTF-8")));
        httpOutputMessage.getBody().write( dicomObject.getFile().toString().getBytes());
    }
}

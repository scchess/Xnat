package org.nrg.xapi.rest.dicomweb;

import org.dcm4che3.data.Attributes;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Lazy
public class MultipartDicomFileMessageConverter extends AbstractHttpMessageConverter< List<DicomObjectI>> {

    private List<HttpMessageConverter<?>> _converters;
    private final static Map<String, String> DICOM_XML_TYPE = createMediaTypes();
    private static Map<String, String> createMediaTypes() {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("type", "\"application/dicom\"");
        return Collections.unmodifiableMap(aMap);
    }
    private final static MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_DICOM = new MediaType("application", "dicom");
    private final static MediaType APPLICATION_DICOM_XML = new MediaType("application", "dicom+xml");

    public MultipartDicomFileMessageConverter() {
        super( MULTIPART_MIXED, MULTIPART_RELATED);
        this._converters = null;
    }

    // for reading from the input message.
    @Override
    protected List<DicomObjectI> readInternal(Class<? extends List<DicomObjectI>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }
    @Override

    protected void writeInternal( List<DicomObjectI> dicomParts, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        
        try {

            HttpHeaders headers = new HttpHeaders();
            Map<String,String> contentTypeArgs = new HashMap<>(1);
//            contentTypeArgs.put("type", "application/dicom+xml");
            String boundary = getBoundary();
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            headers.setContentType( mediaType);

            outputMessage.getBody().write( ("Content-Type: " + headers.getContentType().toString() + "\r\n").getBytes());

            // write preamble
            outputMessage.getBody().write( "\r\n".getBytes());

            for ( DicomObjectI dicomPart: dicomParts) {

                HttpMessageConverter converter = getConverter( dicomPart.getClass(), APPLICATION_DICOM);
                
                if( converter == null) {
                    handleNoConverterFound(dicomPart.getClass(), APPLICATION_DICOM);
                }

//                outputMessage.getBody().write( ("\n--"+ boundary + "\n\n").getBytes());

                outputMessage.getBody().write( ("\r\n--"+ boundary + "\r\n").getBytes());
                outputMessage.getBody().write( ("Content-Type: application/dicom\r\n\r\n").getBytes());

                converter.write( dicomPart, APPLICATION_DICOM, outputMessage);
            }
            outputMessage.getBody().write( ("\r\n--"+ boundary + "--\r\n\r\n").getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNoConverterFound(Class<?> aClass, MediaType mediaType) {
    }

    private HttpMessageConverter<DicomObjectI> getConverter(Class<?> clazz, MediaType mediaType) {
//        for( HttpMessageConverter converter: _converters) {
//            if( converter.canWrite( clazz, mediaType)) {
//                return converter;
//            }
//        }
//        return null;

        return new DicomObjectMessageConverter();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom( clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if( MULTIPART_RELATED.isCompatibleWith(mediaType)) {
            String type = mediaType.getParameter("type");
            if( type != null) {
                type = type.replaceAll("^\"|\"$", "");
                MediaType partMediaType = MediaType.parseMediaType( type);
                if( APPLICATION_DICOM.isCompatibleWith( partMediaType)) {
                    return true;
                }
            }
        }
        return false;
//        return super.canWrite(clazz, mediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return MULTIPART_MIXED.isCompatibleWith(mediaType) || MULTIPART_RELATED.isCompatibleWith( mediaType);
    }

    private  String getBoundary() {
        StringBuffer buf = new StringBuffer(64);
        buf.append("Part_").append('_').append((new Object()).hashCode()).append('.').append(System.currentTimeMillis());
        return buf.toString();
    }

    public static void main(String[] args) {
        MediaType type = MultipartDicomFileMessageConverter.MULTIPART_RELATED;
        Map<String,String> map = new HashMap<>();
        map.put("type", "\"application/dicom\"");
        MediaType type2 = new MediaType("multipart", "related", map);
        System.out.println(type);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( new MediaType( "application", "dicom+xml"));
        headers.set("Content-ID", "1");

        System.out.println( headers);
    }
}


package org.nrg.xapi.rest.dicomweb;

import org.dcm4che3.data.Attributes;
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
public class MultipartDicomMessageConverter extends AbstractHttpMessageConverter< List<Attributes>> {

    private List<HttpMessageConverter<?>> _converters;
    private final static Map<String, String> DICOM_XML_TYPE = createMediaTypes();
    private static Map<String, String> createMediaTypes() {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("type", "\"application/dicom+xml\"");
        return Collections.unmodifiableMap(aMap);
    }
    private final static MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_DICOM_XML = new MediaType("application", "dicom+xml");

    public MultipartDicomMessageConverter(List<HttpMessageConverter<?>> converters) {
        super( MULTIPART_MIXED, MULTIPART_RELATED);
        this._converters = converters;
    }

    // for reading from the input message.
    @Override
    protected List<Attributes> readInternal(Class<? extends List<Attributes>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal( List<Attributes> dicomParts, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        
        try {

            HttpHeaders headers = new HttpHeaders();
            Map<String,String> contentTypeArgs = new HashMap<>(1);
//            contentTypeArgs.put("type", "application/dicom+xml");
            String boundary = getBoundary();
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            headers.setContentType( mediaType);

            outputMessage.getBody().write( ("Content-Type: " + headers.getContentType().toString() + "\n").getBytes());

            // write preamble
            outputMessage.getBody().write( "\n".getBytes());

            for ( Attributes dicomPart: dicomParts) {

                HttpMessageConverter converter = getConverter( dicomPart.getClass(), APPLICATION_DICOM_XML);
                
                if( converter == null) {
                    handleNoConverterFound(dicomPart.getClass(), APPLICATION_DICOM_XML);
                }

//                outputMessage.getBody().write( ("\n--"+ boundary + "\n\n").getBytes());

                outputMessage.getBody().write( ("\n--"+ boundary + "\n").getBytes());
                outputMessage.getBody().write( ("Content-Type: application/dicom+xml\n").getBytes());

                converter.write( dicomPart, APPLICATION_DICOM_XML, outputMessage);
            }
            outputMessage.getBody().write( ("\n--"+ boundary + "--\n\n").getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNoConverterFound(Class<?> aClass, MediaType mediaType) {
    }

    private HttpMessageConverter<Attributes> getConverter(Class<?> clazz, MediaType mediaType) {
//        for( HttpMessageConverter converter: _converters) {
//            if( converter.canWrite( clazz, mediaType)) {
//                return converter;
//            }
//        }
//        return null;

        return new Dicom2XmlMessageConverter();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom( clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
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
        MediaType type = MultipartDicomMessageConverter.MULTIPART_RELATED;
        Map<String,String> map = new HashMap<>();
        map.put("type", "\"application/dicom+xml\"");
        MediaType type2 = new MediaType("multipart", "related", map);
        System.out.println(type);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( new MediaType( "application", "dicom+xml"));
        headers.set("Content-ID", "1");

        System.out.println( headers);
    }
}


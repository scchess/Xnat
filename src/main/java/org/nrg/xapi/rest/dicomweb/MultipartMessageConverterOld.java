package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
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

/**
 * Created by davidmaffitt on 8/17/17.
 */
//@Component
public class MultipartMessageConverterOld extends AbstractHttpMessageConverter< List<BodyPart>> {

    private List<HttpMessageConverter<?>> _converters;
    private final static Map<String, String> DICOM_XML_TYPE = createMediaTypes();
    private static Map<String, String> createMediaTypes() {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("type", "applicationdicom+xml");
        return Collections.unmodifiableMap(aMap);
    }
    private final static MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");

    public MultipartMessageConverterOld(List<HttpMessageConverter<?>> converters) {
        super( MULTIPART_MIXED, MULTIPART_RELATED);
        this._converters = converters;
    }

    // for reading from the input message.
    @Override
    protected List<BodyPart> readInternal(Class<? extends List<BodyPart>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal( List<BodyPart> bodyParts, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        
        try {

            HttpHeaders headers = new HttpHeaders();
            Map<String,String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            headers.setContentType( mediaType);

            outputMessage.getBody().write( ("ContentType: " + headers.getContentType().toString() + "\n").getBytes());

            // write preamble
            outputMessage.getBody().write( "\n".getBytes());

            for (BodyPart bodyPart: bodyParts) {

                HttpMessageConverter converter = getConverter( bodyPart.getBody().getClass(), bodyPart.getMediaType());
                
                if( converter == null) {
                    handleNoConverterFound( bodyPart.getBody().getClass(), bodyPart.getMediaType());
                }
                
                outputMessage.getBody().write( ("\n--"+ boundary + "\n\n").getBytes());

                converter.write( bodyPart.getBody(), bodyPart.getMediaType(), outputMessage);
            }
            outputMessage.getBody().write( ("\n--"+ boundary + "--\n\n").getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNoConverterFound(Class<?> aClass, MediaType mediaType) {
    }

    private HttpMessageConverter<DicomObjectI> getConverter(Class<?> clazz, MediaType mediaType) {
        for( HttpMessageConverter converter: _converters) {
            if( converter.canWrite( clazz, mediaType)) {
                return converter;
            }
        }
        return null;
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
        MediaType type = MultipartMessageConverterOld.MULTIPART_RELATED;
        Map<String,String> map = new HashMap<>();
        map.put("type", "applicationdicom+xml");
        MediaType type2 = new MediaType("multipart", "related", map);
        System.out.println(type);
    }
}


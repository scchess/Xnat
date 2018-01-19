package org.nrg.xapi.rest.dicomweb;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.UID;
import org.dcm4che3.imageio.codec.TransferSyntaxType;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xapi.model.dicomweb.UnsupportedTransferSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Lazy
public class MultipartDicomFileMessageConverter extends AbstractHttpMessageConverter< List<DicomObjectI>> {

    public static final String DEFAULT_DICOM_TSUID = "1.2.840.10008.1.2.1";  // Explicit VR Little Endian.

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
    private final static MediaType APPLICATION_JPEG = new MediaType("application", "jpeg");
    private final static MediaType APPLICATION_DICOM_XML = new MediaType("application", "dicom+xml");

    @Autowired
    private TransCoder transCoder;

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

//            IIORegistry.getDefaultInstance().registerServiceProvider( com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriter.class., javax.imageio.spi.ImageWriterSpi.class);
//            ImagingServiceProviderUtils.register();

            HttpHeaders defaultHeaders = outputMessage.getHeaders();
            MediaType defaultMediaType = MediaType.parseMediaType( defaultHeaders.getFirst("Content-Type"));
            String tsuid = defaultMediaType.getParameter("transfer-syntax");
            tsuid = (tsuid == null)? DEFAULT_DICOM_TSUID: tsuid;

            if( ! transCoder.isSupportedTransferSyntax( tsuid)) {
                throw new UnsupportedTransferSyntaxException( tsuid);
            }

            HttpHeaders headers = new HttpHeaders();
            Map<String,String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            headers.setContentType( mediaType);

//            outputMessage.getBody().write( ("Content-Type: " + headers.getContentType().toString() + "\r\n").getBytes());

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

//                converter.write( dicomPart, APPLICATION_DICOM, outputMessage);
                transCoder.transcode( dicomPart, tsuid, outputMessage.getBody());
//                transCoder.transcode( dicomPart, "1.2.840.10008.1.2.1", outputMessage.getBody());
            }
            outputMessage.getBody().write( ("\r\n--"+ boundary + "--\r\n\r\n").getBytes());


        } catch (IOException e) {
            String msg = "Error streaming dicom.";
            throw new IOException(msg, e);
        } catch( TransCoderException e) {
            throw new HttpMessageNotWritableException(e.getMessage(), e);
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
                    // don't test the tsuid here. let it go so writer can return helpful error message.
//                    String tsuid = mediaType.getParameter("transfer-syntax");
//                    tsuid = (tsuid == null)? UID.ExplicitVRLittleEndian: tsuid;
//
//                    return transCoder.isSupportedTransferSyntax( tsuid);
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


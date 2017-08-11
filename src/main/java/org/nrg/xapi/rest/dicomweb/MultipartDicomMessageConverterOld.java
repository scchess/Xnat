package org.nrg.xapi.rest.dicomweb;

import org.apache.http.HttpEntity;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davidmaffitt on 8/17/17.
 */
//@Component
public class MultipartDicomMessageConverterOld extends AbstractHttpMessageConverter<DicomObjectI[]> {

    private static final Map<String,String> mediatypeParamMap = new HashMap<>(1);
    static {
        mediatypeParamMap.put("type", "dicom");
    }

    private List<HttpMessageConverter<?>> _converters;

    public MultipartDicomMessageConverterOld(List<HttpMessageConverter<?>> converters) {
        super(new MediaType("multipart","related", mediatypeParamMap));
        this._converters = converters;
    }

    // for reading from the input message.
    @Override
    protected DicomObjectI[] readInternal(Class<? extends DicomObjectI[]> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
//        DicomObjectI dicomObject = DicomObjectFactory.create( arg1.getBody());
//        return dicomObject;
        return null;
    }

//    @Override
//    protected void writeInternalold( DicomObjectI[] dicomObjects, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
////        dicomObject.write( httpOutputMessage.getBody());
//
//        Session session = null;
//        MimeMessage message = new MimeMessage(session);
//
//        Multipart mp = new MimeMultipart("related");
//
//        try {
//
//            for (DicomObjectI dicomObject: dicomObjects) {
//                BodyPart bp = new MimeBodyPart();
//                bp.setContent("boundary:" + mp., "application/dicom");
//                //            imgPart.setDataHandler(new DataHandler(imageReference.getImageDataSource()));
//                //            imgPart.setHeader("Content-ID", imageReference.getContentId());
//                mp.addBodyPart(bp);
//            }
//
//            message.setContent( mp);
//            Files.copy( message.getInputStream(), Paths.get("/tmp/message.content"), StandardCopyOption.REPLACE_EXISTING);
//
//            message.writeTo( outputMessage.getBody());
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    protected void writeInternal( DicomObjectI[] dicomObjects, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

//        HttpMessageConverter<DicomObjectI> converter = getConverter( DicomObjectI.class);
        try {

            int i = 0;
            for (DicomObjectI dicomObject: dicomObjects) {

                outputMessage.getBody().write( ("\n"+ getBoundary(i) + "\n\n").getBytes());

                outputMessage.getBody().write( "some content".getBytes());
                i++;
            }
            outputMessage.getBody().write( ("\n"+ getBoundary(i) + "--\n\n").getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private HttpMessageConverter<DicomObjectI> getConverter(Class<DicomObjectI> dicomObjectIClass, MediaType mediaType) {
        for( HttpMessageConverter converter: _converters) {
            if( converter.canWrite( dicomObjectIClass, mediaType)) {
                return converter;
            }
        }
        return null;
    }

    /**
     * This is getting called with class java.util.ArrayList with generic interface java.util.List<E> where E is of type java.lang.Object
     * and not DicomObjectI as hoped.  Not sure why.
     * see: https://stackoverflow.com/questions/3247058/how-to-get-concrete-type-of-a-generic-interface
     *
     * @param clazz
     * @return
     */
    @Override
    protected boolean supports(Class<?> clazz) {
//        Type[] genericInterfaces = clazz.getGenericInterfaces();
//        for (Type genericInterface : genericInterfaces) {
//            if (genericInterface instanceof ParameterizedType) {
//                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
//                for (Type genericType : genericTypes) {
//                    System.out.println("Generic type: " + genericType);
//                }
//            }
//        }
//        return List.class.isAssignableFrom( clazz);
//        return true;
        return clazz.isArray() && DicomObjectI.class.isAssignableFrom(clazz.getComponentType());
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return super.canWrite(mediaType);
    }

    private  String getBoundary( int partNum) {
        StringBuffer buf = new StringBuffer(64);
        buf.append("----=_Part_").append(partNum).append('_').append((new Object()).hashCode()).append('.').append(System.currentTimeMillis());
        return buf.toString();
    }}


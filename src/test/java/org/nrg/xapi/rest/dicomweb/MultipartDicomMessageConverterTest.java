package org.nrg.xapi.rest.dicomweb;

import org.dcm4che3.data.Attributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.xnat.dicom.mizer.service.BaseMizerTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit test the MultipartDicomMessageConverter.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class MultipartDicomMessageConverterTest extends BaseMizerTest {

    /**
     * Test the multipart message format is correct.
     *
     * Uses an implementation of javax.mail.internet.MimeMultipart as the gold standard.
     * Checks to see that it can parse the message and find two bodyparts. The bodypart contents are not checked.
     *
     */
    @Test
    public void multiparFormatTest() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        MultipartDicomMessageConverter mpdmc = new MultipartDicomMessageConverter( converters);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpOutputMessage outputMessage = new HttpOutputMessage() {
            @Override
            public OutputStream getBody() throws IOException {
                return os;
            }

            @Override
            public HttpHeaders getHeaders() {
                return new HttpHeaders();
            }
        };

        DataSource dataSource = new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream( os.toByteArray());
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return "multipart/related";
            }

            @Override
            public String getName() {
                return "Test datasource";
            }
        };

        List<Attributes> parts = new ArrayList<>();
        Attributes attributes = new Attributes();
        parts.add( attributes);
        parts.add( attributes);

        try {
            mpdmc.writeInternal( parts, outputMessage);
//            System.out.println( os.toString());
            MimeMultipart mimeMultipart = new MimeMultipart( dataSource);

            assertEquals(2, mimeMultipart.getCount());

        } catch (IOException | MessagingException e) {
            fail("Unexpected exception " + e);
        }

    }


}

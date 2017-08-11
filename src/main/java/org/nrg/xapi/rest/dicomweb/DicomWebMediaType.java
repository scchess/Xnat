package org.nrg.xapi.rest.dicomweb;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidmaffitt on 8/17/17.
 */
public class DicomWebMediaType {

    private static final Map<String,String> mediatypeParamMap = new HashMap<>(1);
    static {
        mediatypeParamMap.put("type", "\"application/dicom+xml\"");
    }

    public static final MediaType MULTIPARTDICOM_XML = new MediaType("multipart","related", mediatypeParamMap);
    public static final MediaType APPLICATION_JSON = new MediaType("application","dicom+json", mediatypeParamMap);
}

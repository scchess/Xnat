package org.nrg.xapi.rest.dicomweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

//@Component
public abstract  class DicomWebTest {

//    @Autowired
    public DicomWebSearchClient client;

    public DicomWebTest( DicomWebSearchClient client) {
        this.client = client;
    }

    abstract void runTest(String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) ;

}

/*
 * web: org.nrg.xapi.model.users.TestUserSerializationConfig
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.nrg.framework.beans.Beans;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.configuration.RestApiConfig;
import org.nrg.xapi.rest.XapiRestControllerAdvice;
import org.nrg.xnat.configuration.PreferencesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Map;

@Configuration
//@Import(value = {RestApiConfig.class, PreferencesConfig.class})
//@ComponentScan(value = {"org.nrg.xapi.rest","org.nrg.xnat.configuration","org.nrg.xdat.preferences","org.nrg.prefs.services","org.nrg.prefs.repositories"})
public class DicomWebTestConfig {

//    @Bean
//    public DicomWebSearchClient dicomWebSearchClient() {
//        return new DicomWebSearchClient("http://xnat-latest/xapi/dicomweb");
//    }
}

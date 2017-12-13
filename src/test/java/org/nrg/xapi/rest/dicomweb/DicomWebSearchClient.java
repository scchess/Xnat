package org.nrg.xapi.rest.dicomweb;

import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//@Component
public class DicomWebSearchClient {

    private static String service;

    public DicomWebSearchClient( String service) {
        this.service = service;
    }


    public JsonArray doGetJson( String path, String params, String userAuthenticationHeaderValue) throws IOException {

        URL url = new URL(service + path + ( (params == null || params.isEmpty())? "": "?" + encodeParamValues(params)));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/dicom+json");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Authorization", userAuthenticationHeaderValue);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        JsonReader rdr = Json.createReader( conn.getInputStream());

        JsonArray responses = rdr.readArray();
        conn.disconnect();

        return responses;
    }

    /**
     * Assumed params are of form like "modality=MR&PatientName=Tom Jones". The '=' and '&' are ok but the spaces are
     * a problem. Make them '+'.
     *
     * @param params
     * @return
     */
    private String encodeParamValues( String params) {

        return params.replaceAll("[ ]", "+");
    }
}



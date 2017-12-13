package org.nrg.xapi.rest.dicomweb;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.xapi.rest.XapiRestControllerAdvice;
import org.nrg.xnat.configuration.PreferencesConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DicomWebTestConfig.class})
public class TestSearchForSeriesWithStudy {

    DicomWebSearchClient client = new DicomWebSearchClient( "http://xnat-latest/xapi/dicomweb");

    @Test
    public void byModality() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.290843139448249569449245338151";
        Properties p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax T2 Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.290843139448249569449245338151");
        p.setProperty("seriesNumber", "4");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102026");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("seriesNumber", "5");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series",
                "Modality=MR",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void bySeriesInstanceUID() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        Properties p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("seriesNumber", "5");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series",
                "SeriesInstanceUID=1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void bySeriesNumber() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        Properties p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("seriesNumber", "5");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series",
                "SeriesNumber=5",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    @Ignore
    public void byPerformedProcedureStepStartDate() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        Properties p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("seriesNumber", "5");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series",
                "PerformedProcedureStepStartDate=19981012",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPerformedProcedureStepStartTime() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        Properties p = new Properties();
        p.setProperty("modality", "MR");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("seriesNumber", "5");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series",
                "PerformedProcedureStepStartTime=102536",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    public void runTest( String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) {
        try {

            JsonArray responses = client.doGetJson( path, params, userAuthenticationHeaderValue);
            assertEquals("Number of responses", expectedResponses.size(), responses.size());

            for (JsonValue responseValue : responses) {
                JsonSeriesResponse response = new JsonSeriesResponse((JsonObject) responseValue);

                assertTrue(expectedResponses.containsKey(response.seriesInstanceUID));

                Properties expectedProperties = expectedResponses.get(response.seriesInstanceUID);

                response.assertMatch(expectedProperties);
            }

        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

}

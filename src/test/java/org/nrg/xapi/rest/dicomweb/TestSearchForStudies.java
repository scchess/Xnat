package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class TestSearchForStudies {

    DicomWebSearchClient client = new DicomWebSearchClient("http://xnat-latest/xapi/dicomweb");
    @Test
    public void byStudyDate() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "143546");
        p.setProperty("AccessionNumber", "5498216673989931");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "CT\\MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "TCGA-CS-6186");
        p.setProperty("PatientID", "TCGA-CS-6186");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "6");
        p.setProperty("NumberOfInstancesInStudy", "938");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.5826.4001.115716244521609483756859197133";
        p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "212156");
        p.setProperty("AccessionNumber", "2771128247345677");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "CT");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "TCGA-19-1787");
        p.setProperty("PatientID", "TCGA-19-1787");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "3");
        p.setProperty("NumberOfInstancesInStudy", "126");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyDate=20170101",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyTime() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "143546");
        p.setProperty("AccessionNumber", "5498216673989931");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "CT");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "TCGA-CS-6186");
        p.setProperty("PatientID", "TCGA-CS-6186");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "4");
        p.setProperty("NumberOfInstancesInStudy", "818");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyTime=143546",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byAccessionNumber() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.5826.4001.115716244521609483756859197133";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "212156");
        p.setProperty("AccessionNumber", "2771128247345677");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "CT");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "TCGA-19-1787");
        p.setProperty("PatientID", "TCGA-19-1787");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "3");
        p.setProperty("NumberOfInstancesInStudy", "126");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "AccessionNumber=2771128247345677",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byModalitiesInStudy() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "120");
        expectedResponses.put(uid, p);

        uid = "1.2.840.113654.2.45.6231.214187883015805947948563442388671972661";
        p = new Properties();
        p.setProperty("Date", "19900101");
        p.setProperty("Time", "133655");
        p.setProperty("AccessionNumber", "null");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR\\PT");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "petmrSub");
        p.setProperty("PatientID", "petmrSub_PETMR");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "38");
        p.setProperty("NumberOfInstancesInStudy", "101");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "ModalitiesInStudy=MR",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientName() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "120");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "PatientName=LGG-104",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientNameWildcard() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "120");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "PatientName=LGG-1*",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUIDList() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "143546");
        p.setProperty("AccessionNumber", "5498216673989931");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "CT\\MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "TCGA-CS-6186");
        p.setProperty("PatientID", "TCGA-CS-6186");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("StudyID", "null");
        p.setProperty("NumberOfSeries", "6");
        p.setProperty("NumberOfInstancesInStudy", "938");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405\\1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "PatientID=LGG-660",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyID=333",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    public void runTest( String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) {
        try {

            JsonArray responses = client.doGetJson( path, params, userAuthenticationHeaderValue);
            assertEquals("Number of responses", expectedResponses.size(), responses.size());

            for (JsonValue responseValue : responses) {
                JsonStudyResponse response = new JsonStudyResponse((JsonObject) responseValue);

                assertTrue(expectedResponses.containsKey(response.studyInstanceUID));

                Properties expectedProperties = expectedResponses.get(response.studyInstanceUID);

                response.assertMatch(expectedProperties);
            }

        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
}



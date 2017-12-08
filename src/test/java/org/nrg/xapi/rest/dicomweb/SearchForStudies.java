package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class SearchForStudies {

    private static String host = "http://xnat-latest";

    @Test
    public void byStudyDate() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "143546");
        p.setProperty( "AccessionNumber", "5498216673989931");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "CT\\MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "TCGA-CS-6186");
        p.setProperty( "PatientID", "TCGA-CS-6186");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "6");
        p.setProperty( "NumberOfInstances", "938");
        expectedResponses.put( uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.5826.4001.115716244521609483756859197133";
        p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "212156");
        p.setProperty( "AccessionNumber", "2771128247345677");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "CT");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "TCGA-19-1787");
        p.setProperty( "PatientID", "TCGA-19-1787");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "3");
        p.setProperty( "NumberOfInstances", "126");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "StudyDate=20170101",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyTime() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "143546");
        p.setProperty( "AccessionNumber", "5498216673989931");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "CT");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "TCGA-CS-6186");
        p.setProperty( "PatientID", "TCGA-CS-6186");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "4");
        p.setProperty( "NumberOfInstances", "818");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "StudyTime=143546",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byAccessionNumber() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.5826.4001.115716244521609483756859197133";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "212156");
        p.setProperty( "AccessionNumber", "2771128247345677");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "CT");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "TCGA-19-1787");
        p.setProperty( "PatientID", "TCGA-19-1787");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "3");
        p.setProperty( "NumberOfInstances", "126");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "AccessionNumber=2771128247345677",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byModalitiesInStudy() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "101452");
        p.setProperty( "AccessionNumber", "7408465417966656");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-104");
        p.setProperty( "PatientID", "LGG-104");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "120");
        expectedResponses.put( uid, p);

        uid = "1.2.840.113654.2.45.6231.214187883015805947948563442388671972661";
        p = new Properties();
        p.setProperty( "Date", "19900101");
        p.setProperty( "Time", "133655");
        p.setProperty( "AccessionNumber", "null");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR\\PT");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "petmrSub");
        p.setProperty( "PatientID", "petmrSub_PETMR");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "38");
        p.setProperty( "NumberOfInstances", "101");
        expectedResponses.put( uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        p = new Properties();
        p.setProperty( "Date", "19981012");
        p.setProperty( "Time", "120227");
        p.setProperty( "AccessionNumber", "9324247368316197");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-660");
        p.setProperty( "PatientID", "LGG-660");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "333");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "20");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "ModalitiesInStudy=MR",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientName() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "101452");
        p.setProperty( "AccessionNumber", "7408465417966656");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-104");
        p.setProperty( "PatientID", "LGG-104");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "120");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "PatientName=LGG-104",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientNameWildcard() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "101452");
        p.setProperty( "AccessionNumber", "7408465417966656");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-104");
        p.setProperty( "PatientID", "LGG-104");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "120");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "PatientName=LGG-1*",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty( "Date", "19981012");
        p.setProperty( "Time", "120227");
        p.setProperty( "AccessionNumber", "9324247368316197");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-660");
        p.setProperty( "PatientID", "LGG-660");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "333");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "20");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUIDList() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405";
        Properties p = new Properties();
        p.setProperty( "Date", "20170101");
        p.setProperty( "Time", "143546");
        p.setProperty( "AccessionNumber", "5498216673989931");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "CT\\MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "TCGA-CS-6186");
        p.setProperty( "PatientID", "TCGA-CS-6186");
        p.setProperty( "PatientDOB", "2017");
        p.setProperty( "PatientGender", "female");
        p.setProperty( "StudyID", "null");
        p.setProperty( "NumberOfSeries", "6");
        p.setProperty( "NumberOfInstances", "938");
        expectedResponses.put( uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        p = new Properties();
        p.setProperty( "Date", "19981012");
        p.setProperty( "Time", "120227");
        p.setProperty( "AccessionNumber", "9324247368316197");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-660");
        p.setProperty( "PatientID", "LGG-660");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "333");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "20");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405\\1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty( "Date", "19981012");
        p.setProperty( "Time", "120227");
        p.setProperty( "AccessionNumber", "9324247368316197");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-660");
        p.setProperty( "PatientID", "LGG-660");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "333");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "20");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "PatientID=LGG-660",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty( "Date", "19981012");
        p.setProperty( "Time", "120227");
        p.setProperty( "AccessionNumber", "9324247368316197");
        p.setProperty( "InstanceAvailability", "ONLINE");
        p.setProperty( "ModalitiesInStudy", "MR");
        p.setProperty( "ReferringPhysicianName", "ignore");
        p.setProperty( "TimeZoneOffset", "ignore");
        p.setProperty( "RetrieveURL", "ignore");
        p.setProperty( "PatientName", "LGG-660");
        p.setProperty( "PatientID", "LGG-660");
        p.setProperty( "PatientDOB", "1960");
        p.setProperty( "PatientGender", "male");
        p.setProperty( "StudyID", "333");
        p.setProperty( "NumberOfSeries", "2");
        p.setProperty( "NumberOfInstances", "20");
        expectedResponses.put( uid, p);

        runTest( host,
                "/xapi/dicomweb/studies",
                "StudyID=333",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    public void runTest( String host, String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) {
        try {

            URL url = new URL(host + path + "?" + params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/dicom+json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Authorization", userAuthenticationHeaderValue);

            if (conn.getResponseCode() != 200) {
                fail("Failed : HTTP error code : " + conn.getResponseCode());
            }

            JsonReader rdr = Json.createReader(conn.getInputStream());

            JsonArray responses = rdr.readArray();
            assertEquals( "Number of responses", expectedResponses.size(), responses.size());

            for( JsonValue responseValue: responses) {
                JsonResponse response = new JsonResponse( (JsonObject) responseValue);

                assertTrue( expectedResponses.containsKey( response.studyInstanceUID));

                Properties expectedProperties = expectedResponses.get( response.studyInstanceUID);

                assertEqualsCustom( "date", expectedProperties.getProperty("Date"), response.date);
                assertEqualsCustom( "time", expectedProperties.getProperty("Time"), response.time);
                assertEqualsCustom( "accessionNumber", expectedProperties.getProperty("AccessionNumber"), response.accessionNumber);
                assertEqualsCustom( "instanceAvailability", expectedProperties.getProperty("InstanceAvailability"), response.instanceAvailability);
                assertEqualsCustom( "modalitiesInStudy", expectedProperties.getProperty("ModalitiesInStudy"), response.modalitiesInStudy);
                assertEqualsCustom( "referringPhysicianName", expectedProperties.getProperty("ReferringPhysicianName"), response.referringPhysicianName);
                assertEqualsCustom( "timeZoneOffset", expectedProperties.getProperty("TimeZoneOffset"), response.timeZoneOffset);
                assertEqualsCustom( "retrieveURL", expectedProperties.getProperty("RetrieveURL"), response.retrieveURL);
                assertEqualsCustom( "patientName", expectedProperties.getProperty("PatientName"), response.patientName);
                assertEqualsCustom( "patientID", expectedProperties.getProperty("PatientID"), response.patientID);
                assertEqualsCustom( "patientDOB", expectedProperties.getProperty("PatientDOB"), response.patientDOB);
                assertEqualsCustom( "patientGender", expectedProperties.getProperty("PatientGender"), response.patientGender);
                assertEqualsCustom( "studyID", expectedProperties.getProperty("StudyID"), response.studyID);
                assertEqualsCustom( "numberOfSeries", expectedProperties.getProperty("NumberOfSeries"), response.numberOfSeries);
                assertEqualsCustom( "numberOfInstances", expectedProperties.getProperty("NumberOfInstances"), response.numberOfInstances);
            }

            conn.disconnect();

        } catch ( IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    public void assertEqualsCustom( String msg, String expected, String actual) {
        switch( expected) {
            case "ignore":
                return;
            case "null":
                assertNull(msg, actual);
                break;
            default:
                assertEquals( msg, expected, actual);
        }
    }

    protected class JsonResponse {
        String studyInstanceUID;
        String date;
        String time;
        String accessionNumber;
        String instanceAvailability;
        String modalitiesInStudy;
        String referringPhysicianName;
        String timeZoneOffset;
        String retrieveURL;
        String patientName;
        String patientID;
        String patientDOB;
        String patientGender;
        String studyID;
        String numberOfSeries;
        String numberOfInstances;

        public JsonResponse( JsonObject response) {
            studyInstanceUID       = getValueFromJson( response, "0020000D");
            date                   = getValueFromJson( response, "00080020");
            time                   = getValueFromJson( response, "00080030");
            accessionNumber        = getValueFromJson( response, "00080050");
            instanceAvailability   = getValueFromJson( response, "00080056");
            modalitiesInStudy      = getValueFromJson( response, "00080061");
            referringPhysicianName = getValueFromJson( response, "00080090");
            timeZoneOffset         = getValueFromJson( response, "00080201");
            retrieveURL            = getValueFromJson( response, "00081190");
            patientName            = getPNValueFromJson( response, "00100010");
            patientID              = getValueFromJson( response, "00100020");
            patientDOB             = getValueFromJson( response, "00100030");
            patientGender          = getValueFromJson( response, "00100040");
            studyID                = getValueFromJson( response, "00200010");
            numberOfSeries         = getIntegerValueFromJson( response, "00201206");
            numberOfInstances      = getIntegerValueFromJson( response, "00201208");
        }
    }

    public String getValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonString(0) != null) {
                    value = response.getJsonObject(tag).getJsonArray("Value").getJsonString(0).getString();
                }
            }
        }
        return value;
    }

    public String getIntegerValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonNumber(0) != null) {
                    value = response.getJsonObject(tag).getJsonArray("Value").getJsonNumber(0).toString();
                }
            }
        }
        return value;
    }

    public String getPNValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0) != null) {
                    if( response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0).getJsonString("Alphabetic") != null) {
                        value = response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0).getJsonString("Alphabetic").getString();
                    }
                }
            }
        }
        return value;
    }

}

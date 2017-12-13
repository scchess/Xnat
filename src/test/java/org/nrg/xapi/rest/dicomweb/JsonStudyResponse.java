package org.nrg.xapi.rest.dicomweb;

import javax.json.JsonObject;
import java.util.Properties;

public class JsonStudyResponse extends JsonResponse {
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
    String numberOfInstancesInStudy;

    public JsonStudyResponse( JsonObject response) {
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
        numberOfInstancesInStudy = getIntegerValueFromJson( response, "00201208");
    }

    public void assertMatch( Properties expectedProperties) {
        assertEqualsCustom( "date", expectedProperties.getProperty("Date"), date);
        assertEqualsCustom( "time", expectedProperties.getProperty("Time"), time);
        assertEqualsCustom( "accessionNumber", expectedProperties.getProperty("AccessionNumber"), accessionNumber);
        assertEqualsCustom( "instanceAvailability", expectedProperties.getProperty("InstanceAvailability"), instanceAvailability);
        assertEqualsCustom( "modalitiesInStudy", expectedProperties.getProperty("ModalitiesInStudy"), modalitiesInStudy);
        assertEqualsCustom( "referringPhysicianName", expectedProperties.getProperty("ReferringPhysicianName"), referringPhysicianName);
        assertEqualsCustom( "timeZoneOffset", expectedProperties.getProperty("TimeZoneOffset"), timeZoneOffset);
        assertEqualsCustom( "retrieveURL", expectedProperties.getProperty("RetrieveURL"), retrieveURL);
        assertEqualsCustom( "patientName", expectedProperties.getProperty("PatientName"), patientName);
        assertEqualsCustom( "patientID", expectedProperties.getProperty("PatientID"), patientID);
        assertEqualsCustom( "patientDOB", expectedProperties.getProperty("PatientDOB"), patientDOB);
        assertEqualsCustom( "patientGender", expectedProperties.getProperty("PatientGender"), patientGender);
        assertEqualsCustom( "studyID", expectedProperties.getProperty("StudyID"), studyID);
        assertEqualsCustom( "numberOfSeries", expectedProperties.getProperty("NumberOfSeries"), numberOfSeries);
        assertEqualsCustom( "numberOfInstancesInStudy", expectedProperties.getProperty("NumberOfInstancesInStudy"), numberOfInstancesInStudy);
    }
}

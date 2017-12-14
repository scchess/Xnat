package org.nrg.xapi.rest.dicomweb;

import javax.json.JsonObject;
import java.util.Properties;

/**
 * This is a mash-up of Study and Series response.  This would be less redundant if we had java 1.8 and could make this
 * a mixin (java's interfaces with default implementations.)
 *
 */
public class JsonStudySeriesResponse extends JsonResponse {
    // Study properties
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

    // Series properties
    String modality;
    String seriesInstanceUID;
    String seriesNumber;
    String performedProcedureStepStartDate;
    String performedProcedureStepStartTime;
//    String timeZoneOffset;
    String seriesDescription;
//    String retrieveURL;
    String numberOfInstancesInSeries;


    public JsonStudySeriesResponse(JsonObject response) {
        studyInstanceUID       = getValueFromJson( response, "0020000D");
        date                   = getValueFromJson( response, "00080020");
        time                   = getValueFromJson( response, "00080030");
        accessionNumber        = getValueFromJson( response, "00080050");
        instanceAvailability   = getValueFromJson( response, "00080056");
        modalitiesInStudy      = getValueFromJson( response, "00080061");
//        referringPhysicianName = getValueFromJson( response, "00080090");
//        timeZoneOffset         = getValueFromJson( response, "00080201");
//        retrieveURL            = getValueFromJson( response, "00081190");
        patientName            = getPNValueFromJson( response, "00100010");
        patientID              = getValueFromJson( response, "00100020");
        patientDOB             = getValueFromJson( response, "00100030");
        patientGender          = getValueFromJson( response, "00100040");
        studyID                = getValueFromJson( response, "00200010");
        numberOfSeries         = getIntegerValueFromJson( response, "00201206");
        numberOfInstancesInStudy = getIntegerValueFromJson( response, "00201208");

        seriesInstanceUID                = getValueFromJson( response, "0020000E");
        modality                         = getValueFromJson( response, "00080060");
        seriesNumber                     = getIntegerValueFromJson( response, "00200011");
        performedProcedureStepStartDate  = getValueFromJson( response, "00400244");
        performedProcedureStepStartTime  = getValueFromJson( response, "00400245");
//        timeZoneOffset                   = getValueFromJson( response, "00080201");
        seriesDescription                = getValueFromJson( response, "0008103E");
//        retrieveURL                      = getValueFromJson( response, "00081190");
        numberOfInstancesInSeries        = getIntegerValueFromJson( response, "00201209");

    }

    public void assertMatch( Properties expectedProperties) {
        assertEqualsCustom( "date", expectedProperties.getProperty("Date"), date);
        assertEqualsCustom( "time", expectedProperties.getProperty("Time"), time);
        assertEqualsCustom( "accessionNumber", expectedProperties.getProperty("AccessionNumber"), accessionNumber);
        assertEqualsCustom( "instanceAvailability", expectedProperties.getProperty("InstanceAvailability"), instanceAvailability);
        assertEqualsCustom( "modalitiesInStudy", expectedProperties.getProperty("ModalitiesInStudy"), modalitiesInStudy);
//        assertEqualsCustom( "referringPhysicianName", expectedProperties.getProperty("ReferringPhysicianName"), referringPhysicianName);
//        assertEqualsCustom( "timeZoneOffset", expectedProperties.getProperty("TimeZoneOffset"), timeZoneOffset);
//        assertEqualsCustom( "retrieveURL", expectedProperties.getProperty("RetrieveURL"), retrieveURL);
        assertEqualsCustom( "patientName", expectedProperties.getProperty("PatientName"), patientName);
        assertEqualsCustom( "patientID", expectedProperties.getProperty("PatientID"), patientID);
        assertEqualsCustom( "patientDOB", expectedProperties.getProperty("PatientDOB"), patientDOB);
        assertEqualsCustom( "patientGender", expectedProperties.getProperty("PatientGender"), patientGender);
        assertEqualsCustom( "studyID", expectedProperties.getProperty("StudyID"), studyID);
        assertEqualsCustom( "numberOfSeries", expectedProperties.getProperty("NumberOfSeries"), numberOfSeries);
        assertEqualsCustom( "numberOfInstancesInStudy", expectedProperties.getProperty("numberOfInstancesInStudy"), numberOfInstancesInStudy);

        assertEqualsCustom( "seriesInstanceUID", expectedProperties.getProperty("seriesInstanceUID"), seriesInstanceUID);
        assertEqualsCustom( "modality", expectedProperties.getProperty("modality"), modality);
        assertEqualsCustom( "seriesNumber", expectedProperties.getProperty("seriesNumber"), seriesNumber);
        assertEqualsCustom( "performedProcedureStepStartDate", expectedProperties.getProperty("performedProcedureStepStartDate"), performedProcedureStepStartDate);
        assertEqualsCustom( "performedProcedureStepStartTime", expectedProperties.getProperty("performedProcedureStepStartTime"), performedProcedureStepStartTime);
//        assertEqualsCustom( "timeZoneOffset", expectedProperties.getProperty("timeZoneOffset"), timeZoneOffset);
        assertEqualsCustom( "seriesDescription", expectedProperties.getProperty("seriesDescription"), seriesDescription);
//        assertEqualsCustom( "retrieveURL", expectedProperties.getProperty("retrieveURL"), retrieveURL);
        assertEqualsCustom( "numberOfInstancesInSeries", expectedProperties.getProperty("numberOfInstancesInSeries"), numberOfInstancesInSeries);

    }
}

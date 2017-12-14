package org.nrg.xapi.rest.dicomweb;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class TestSearchForSeriesWithoutStudy {

    DicomWebSearchClient client = new DicomWebSearchClient("http://xnat-latest/xapi/dicomweb");
    @Test
    public void byStudyDate() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "TSE_T2_RST_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "3");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "121633");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "T1_FLASH_3D_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "4");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "122524");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "StudyDate=19981012",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyTime() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "TSE_T2_RST_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "3");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "121633");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "T1_FLASH_3D_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "4");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "122524");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "StudyTime=120227",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byAccessionNumber() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.290843139448249569449245338151";
        Properties p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "Gad Ax T2 Straight");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.290843139448249569449245338151");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "4");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "120");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102026");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "5");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "120");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "AccessionNumber=7408465417966656",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byModalitiesInStudy() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "ModalitiesInStudy=CR",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientName() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "PatientName=Made Up",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientNameWildcard() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "PatientName=*ade*",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyInstanceUIDList() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "TSE_T2_RST_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "3");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "121633");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "T1_FLASH_3D_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "4");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "122524");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258\\1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPatientID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "PatientID=LIDC-IDRI-0080",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byStudyID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "TSE_T2_RST_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.158892526693231667871677345309");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "3");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "121633");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995";
        p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "T1_FLASH_3D_AXIAL");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.236334958406185703813315917995");
        p.setProperty("StudyID", "333");
        p.setProperty("seriesNumber", "4");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "20");
        p.setProperty("numberOfInstancesInSeries", "10");
        p.setProperty("performedProcedureStepStartDate", "19981012");
        p.setProperty("performedProcedureStepStartTime", "122524");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "StudyID=333",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byModality() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "Modality=CR",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void bySeriesInstanceUID() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.2.840.113654.2.45.6231.131383433702021729543060578955026044642";
        Properties p = new Properties();
        p.setProperty("Date", "19900101");
        p.setProperty("Time", "133655");
        p.setProperty("AccessionNumber", "null");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR\\PT");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "PET-70min_in");
        p.setProperty("PatientName", "petmrSub");
        p.setProperty("PatientID", "petmrSub_PETMR");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.2.840.113654.2.45.6231.214187883015805947948563442388671972661");
        p.setProperty("seriesInstanceUID", "1.2.840.113654.2.45.6231.131383433702021729543060578955026044642");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "6");
        p.setProperty("NumberOfSeries", "38");
        p.setProperty("numberOfInstancesInStudy", "101");
        p.setProperty("numberOfInstancesInSeries", "3");
        p.setProperty("performedProcedureStepStartDate", "19900101");
        p.setProperty("performedProcedureStepStartTime", "134306");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "SeriesInstanceUID=1.2.840.113654.2.45.6231.131383433702021729543060578955026044642",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void bySeriesNumber() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.2.840.113654.2.45.6231.33136623789078105380905847125606409204";
        Properties p = new Properties();
        p.setProperty("Date", "19900101");
        p.setProperty("Time", "133655");
        p.setProperty("AccessionNumber", "null");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR\\PT");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "UTE_MRAC_UMAP");
        p.setProperty("PatientName", "petmrSub");
        p.setProperty("PatientID", "petmrSub_PETMR");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("studyInstanceUID", "1.2.840.113654.2.45.6231.214187883015805947948563442388671972661");
        p.setProperty("seriesInstanceUID", "1.2.840.113654.2.45.6231.33136623789078105380905847125606409204");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "5");
        p.setProperty("NumberOfSeries", "38");
        p.setProperty("numberOfInstancesInStudy", "101");
        p.setProperty("numberOfInstancesInSeries", "3");
        p.setProperty("performedProcedureStepStartDate", "19900101");
        p.setProperty("performedProcedureStepStartTime", "134259");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002";
        p = new Properties();
        p.setProperty("Date", "20170101");
        p.setProperty("Time", "101452");
        p.setProperty("AccessionNumber", "7408465417966656");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "MR");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "Gad Ax SPGR Straight");
        p.setProperty("PatientName", "LGG-104");
        p.setProperty("PatientID", "LGG-104");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "5");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("numberOfInstancesInStudy", "120");
        p.setProperty("numberOfInstancesInSeries", "60");
        p.setProperty("performedProcedureStepStartDate", "null");
        p.setProperty("performedProcedureStepStartTime", "102536");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "SeriesNumber=5",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPerformedProcedureStepStartDate() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "null");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "PerformedProcedureStepStartDate=20000101",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    @Test
    public void byPerformedProcedureStepStartTime() {
        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315";
        Properties p = new Properties();
        p.setProperty("Date", "20000101");
        p.setProperty("Time", "null");
        p.setProperty("AccessionNumber", "2819497684894126");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("modality", "CR");
        p.setProperty("ModalitiesInStudy", "CR");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("seriesDescription", "null");
        p.setProperty("PatientName", "Made Up");
        p.setProperty("PatientID", "LIDC-IDRI-0080");
        p.setProperty("PatientDOB", "2017");
        p.setProperty("PatientGender", "female");
        p.setProperty("studyInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.897501779652275380357570104258");
        p.setProperty("seriesInstanceUID", "1.3.6.1.4.1.14519.5.2.1.6279.6001.525084244168015381779362827315");
        p.setProperty("StudyID", "null");
        p.setProperty("seriesNumber", "3001586");
        p.setProperty("NumberOfSeries", "1");
        p.setProperty("numberOfInstancesInStudy", "1");
        p.setProperty("numberOfInstancesInSeries", "1");
        p.setProperty("performedProcedureStepStartDate", "20000101");
        p.setProperty("performedProcedureStepStartTime", "131415");
        p.setProperty("retrieveURL", "ignore");
        p.setProperty("timeZoneOffset", "ignore");
        expectedResponses.put(uid, p);

        runTest("/series",
                "PerformedProcedureStepStartTime=131415",
                "Basic dXNlcjI6dXNlcjI=",
                expectedResponses);
    }

    public void runTest( String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) {
        try {

            JsonArray responses = client.doGetJson( path, params, userAuthenticationHeaderValue);
            assertEquals("Number of responses", expectedResponses.size(), responses.size());

            for (JsonValue responseValue : responses) {
                JsonStudySeriesResponse response = new JsonStudySeriesResponse((JsonObject) responseValue);

                assertTrue(expectedResponses.containsKey(response.seriesInstanceUID));

                Properties expectedProperties = expectedResponses.get(response.seriesInstanceUID);

                response.assertMatch(expectedProperties);
            }

        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
}



package org.nrg.xapi.rest.dicomweb;

import javax.json.JsonObject;
import java.util.Properties;

public class JsonSeriesResponse extends JsonResponse {
    String modality;
    String seriesInstanceUID;
    String seriesNumber;
    String performedProcedureStepStartDate;
    String performedProcedureStepStartTime;
    String timeZoneOffset;
    String seriesDescription;
    String retrieveURL;
    String numberOfInstancesInSeries;

    public JsonSeriesResponse(JsonObject response) {
        seriesInstanceUID                = getValueFromJson( response, "0020000E");
        modality                         = getValueFromJson( response, "00080060");
        seriesNumber                     = getIntegerValueFromJson( response, "00200011");
        performedProcedureStepStartDate  = getValueFromJson( response, "00400244");
        performedProcedureStepStartTime  = getValueFromJson( response, "00400245");
        timeZoneOffset                   = getValueFromJson( response, "00080201");
        seriesDescription                = getValueFromJson( response, "0008103E");
        retrieveURL                      = getValueFromJson( response, "00081190");
        numberOfInstancesInSeries        = getIntegerValueFromJson( response, "00201209");
    }

    public void assertMatch( Properties expectedProperties) {
        assertEqualsCustom( "seriesInstanceUID", expectedProperties.getProperty("seriesInstanceUID"), seriesInstanceUID);
        assertEqualsCustom( "modality", expectedProperties.getProperty("modality"), modality);
        assertEqualsCustom( "seriesNumber", expectedProperties.getProperty("seriesNumber"), seriesNumber);
        assertEqualsCustom( "performedProcedureStepStartDate", expectedProperties.getProperty("performedProcedureStepStartDate"), performedProcedureStepStartDate);
        assertEqualsCustom( "performedProcedureStepStartTime", expectedProperties.getProperty("performedProcedureStepStartTime"), performedProcedureStepStartTime);
        assertEqualsCustom( "timeZoneOffset", expectedProperties.getProperty("timeZoneOffset"), timeZoneOffset);
        assertEqualsCustom( "seriesDescription", expectedProperties.getProperty("seriesDescription"), seriesDescription);
        assertEqualsCustom( "retrieveURL", expectedProperties.getProperty("retrieveURL"), retrieveURL);
        assertEqualsCustom( "numberOfInstancesInSeries", expectedProperties.getProperty("numberOfInstancesInSeries"), numberOfInstancesInSeries);
    }
}

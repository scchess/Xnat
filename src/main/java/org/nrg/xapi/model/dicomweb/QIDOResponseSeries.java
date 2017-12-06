package org.nrg.xapi.model.dicomweb;

import org.dcm4che3.data.ElementDictionary;

public class QIDOResponseSeries extends QIDOResponse {


    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, ElementDictionary.vrOf(0x00080005,null), value ); }

    public String getModality() { return getString( 0x00080060); }
    public void setModality(String value) { setString( 0x00080060, ElementDictionary.vrOf(0x00080060, null), value ); }

    public String getSeriesDescription() { return getString(0x0008103E); }
    public void setSeriesDescription(String value) { setString( 0x0008103E, ElementDictionary.vrOf(0x0008103E, null), value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

    public String getRetrieveURL() { return getString(0x00081190); }
    public void setRetrieveURL(String value) { setString( 0x00081190, ElementDictionary.vrOf(0x00081190, null), value ); }

    public String getSeriesInstanceUID() { return getString(0x0020000E); }
    public void setSeriesInstanceUID(String value) { setString( 0x0020000E, ElementDictionary.vrOf(0x0020000E, null), value ); }

    public String getSeriesNumber() { return getString(0x00200011); }
    public void setSeriesNumber(String value) { setString( 0x00200011, ElementDictionary.vrOf(0x00200011, null), value ); }

    public String getNumberOfSeriesRelatedInstances() { return getString(0x00201209); }
    public void setNumberOfSeriesRelatedInstances(int value) { setInt( 0x00201209, ElementDictionary.vrOf(0x00201209, null), value ); }

    public String getPerformedProcedureStepStartDate() { return getString(0x00400244); }
    public void setPerformedProcedureStepStartDate(String value) { setString( 0x00400244, ElementDictionary.vrOf(0x00400244, null), value ); }
    public void setPerformedProcedureStepStartDate(Object obj) { setString( 0x00400244, ElementDictionary.vrOf(0x00400244, null), (obj == null)? null:dateFormat.format( obj) ); }

    public String getPerformedProcedureStepStartTime() { return getString(0x00400245); }
    public void setPerformedProcedureStepStartTime(String value) { setString( 0x00400245, ElementDictionary.vrOf(0x00400245, null), value ); }
    public void setPerformedProcedureStepStartTime(Object obj) { setString( 0x00400245, ElementDictionary.vrOf(0x00400245, null), (obj == null)? null:timeFormat.format( obj) ); }
}

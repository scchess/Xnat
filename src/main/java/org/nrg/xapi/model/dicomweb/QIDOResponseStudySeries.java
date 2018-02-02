package org.nrg.xapi.model.dicomweb;

import org.dcm4che3.data.ElementDictionary;

public class QIDOResponseStudySeries extends QIDOResponse {

    // Study
    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, ElementDictionary.vrOf(0x00080005,null), value ); }

    public String getStudyDate() { return getString( 0x00080020); }
    public void setStudyDate(String value) { setString( 0x00080020, ElementDictionary.vrOf(0x00080020, null), value ); }
    public void setStudyDate(Object obj) { setString( 0x00080020, ElementDictionary.vrOf(0x00080020, null), (obj == null)? null:dateFormat.format( obj) ); }

    public String getStudyTime() { return getString(0x00080030); }
    public void setStudyTime(String value) { setString( 0x00080030, ElementDictionary.vrOf(0x00080030, null), value ); }
    public void setStudyTime(Object obj) { setString( 0x00080030, ElementDictionary.vrOf(0x00080030, null), (obj == null)? null:timeFormat.format( obj) ); }

    public String getAccessionNumber() { return getString(0x00080050); }
    public void setAccessionNumber(String value) { setString( 0x00080050, ElementDictionary.vrOf(0x00080050, null), value ); }

    public String getInstanceAvailability() { return getString(0x00080056); }
    public void setInstanceAvailability(String value) { setString( 0x00080056, ElementDictionary.vrOf(0x00080056, null), value ); }

    public String getModalitiesInStudy() { return getString(0x00080061); }
    public void setModalitiesInStudy(String value) { setString( 0x00080061, ElementDictionary.vrOf(0x00080061, null), value ); }

    public String getReferringPhysiciansName() { return getString(0x00080090); }
    public void setReferringPhysiciansName(String value) { setString( 0x00080090, ElementDictionary.vrOf(0x00080090, null), value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

    public String getRetrieveURL() { return getString(0x00081190); }
    public void setRetrieveURL(String value) { setString( 0x00081190, ElementDictionary.vrOf(0x00081190, null), value ); }

    public String getPatientsName() { return getString(0x00100010); }
    public void setPatientsName(String value) { setString( 0x00100010, ElementDictionary.vrOf(0x00100010, null), value ); }

    public String getPatientID() { return getString(0x00100020); }
    public void setPatientID(String value) { setString( 0x00100020, ElementDictionary.vrOf(0x00100020, null), value ); }

    public String getPatientsBirthDate() { return getString(0x00100030); }
    public void setPatientsBirthDate(String value) { setString( 0x00100030, ElementDictionary.vrOf(0x00100030, null), value ); }

    public String getPatientsSex() { return getString(0x00100040); }
    public void setPatientsSex(String value) {
        String gender = null;
        if( value == null || "".equals(value)) {
            gender = value;
        }
        else {
            switch (value.toLowerCase()) {
                case "male":
                case "m":
                    gender = "M";
                    break;
                case "female":
                case "f":
                    gender = "F";
                    break;
                default:
                    gender = "O";
            }
        }
        setString( 0x00100040, ElementDictionary.vrOf(0x00100040, null), gender );
    }

    public String getStudyInstanceUID() { return getString(0x0020000D); }
    public void setStudyInstanceUID(String value) { setString( 0x0020000D, ElementDictionary.vrOf(0x0020000D, null), value ); }

    public String getStudyID() { return getString(0x00200010); }
    public void setStudyID(String value) { setString( 0x00200010, ElementDictionary.vrOf(0x00200010, null), value ); }

//    public String getNumberOfStudyRelatedSeries() { return getString(0x00201206); }
    public int getNumberOfStudyRelatedSeries() { return getInt(0x00201206, 0); }
    public void setNumberOfStudyRelatedSeries(String value) { setString( 0x00201206, ElementDictionary.vrOf(0x00201206, null), value ); }
    public void setNumberOfStudyRelatedSeries(int intValue) { setInt( 0x00201206, ElementDictionary.vrOf(0x00201206, null), intValue ); }

//    public String getNumberOfStudyRelatedInstances() { return getString(0x00201208); }
    public int getNumberOfStudyRelatedInstances() { return getInt(0x00201208, 0); }
    public void setNumberOfStudyRelatedInstances(String value) { setString( 0x00201208, ElementDictionary.vrOf(0x00201208, null), value ); }
    public void setNumberOfStudyRelatedInstances(int intValue) { setInt( 0x00201208, ElementDictionary.vrOf(0x00201208, null), intValue ); }

    // Series
    public String getModality() { return getString( 0x00080060); }
    public void setModality(String value) { setString( 0x00080060, ElementDictionary.vrOf(0x00080060, null), value ); }

    public String getSeriesDescription() { return getString(0x0008103E); }
    public void setSeriesDescription(String value) { setString( 0x0008103E, ElementDictionary.vrOf(0x0008103E, null), value ); }

//    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
//    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

//    public String getRetrieveURL() { return getString(0x00081190); }
//    public void setRetrieveURL(String value) { setString( 0x00081190, ElementDictionary.vrOf(0x00081190, null), value ); }

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

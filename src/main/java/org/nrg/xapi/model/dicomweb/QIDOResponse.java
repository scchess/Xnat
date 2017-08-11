package org.nrg.xapi.model.dicomweb;

/**
 * Created by davidmaffitt on 8/3/17.
 */
public class QIDOResponse {
    private String specificCharacterSet;           // (0008,0005)
    private String studyDate;                      // (0008,0020)
    private String studyTime;                      // (0008,0030)
    private String accessionNumber;                // (0008,0050)
    private String instanceAvailability;           // (0008,0056)
    private String modalitiesInStudy;              // (0008,0061)
    private String referringPhysiciansName;        // (0008,0090)
    private String timezoneOffsetFromUTC;          // (0008,0201)
    private String retrieveURL;                    // (0008,1190)
    private String patientsName;                   // (0010,0010)
    private String patientID;                      // (0010,0020)
    private String patientsBirthDate;              // (0010,0030)
    private String patientsSex;                    // (0010,0040)
    private String studyInstanceUID;               // (0020,000D)
    private String studyID;                        // (0020,0010)
    private String numberOfStudyRelatedSeries;     // (0020,1206)
    private String numberOfStudyRelatedInstances;  // (0020,1208)

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"


    public String getSpecificCharacterSet() {
        return specificCharacterSet;
    }

    public void setSpecificCharacterSet(String specificCharacterSet) {
        this.specificCharacterSet = specificCharacterSet;
    }

    public String getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getInstanceAvailability() {
        return instanceAvailability;
    }

    public void setInstanceAvailability(String instanceAvailability) {
        this.instanceAvailability = instanceAvailability;
    }

    public String getModalitiesInStudy() {
        return modalitiesInStudy;
    }

    public void setModalitiesInStudy(String modalitiesInStudy) {
        this.modalitiesInStudy = modalitiesInStudy;
    }

    public String getReferringPhysiciansName() {
        return referringPhysiciansName;
    }

    public void setReferringPhysiciansName(String referringPhysiciansName) {
        this.referringPhysiciansName = referringPhysiciansName;
    }

    public String getTimezoneOffsetFromUTC() {
        return timezoneOffsetFromUTC;
    }

    public void setTimezoneOffsetFromUTC(String timezoneOffsetFromUTC) {
        this.timezoneOffsetFromUTC = timezoneOffsetFromUTC;
    }

    public String getRetrieveURL() {
        return retrieveURL;
    }

    public void setRetrieveURL(String retrieveURL) {
        this.retrieveURL = retrieveURL;
    }

    public String getPatientsName() {
        return patientsName;
    }

    public void setPatientsName(String patientsName) {
        this.patientsName = patientsName;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientsBirthDate() {
        return patientsBirthDate;
    }

    public void setPatientsBirthDate(String patientsBirthDate) {
        this.patientsBirthDate = patientsBirthDate;
    }

    public String getPatientsSex() {
        return patientsSex;
    }

    public void setPatientsSex(String patientsSex) {
        this.patientsSex = patientsSex;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getStudyID() {
        return studyID;
    }

    public void setStudyID(String studyID) {
        this.studyID = studyID;
    }

    public String getNumberOfStudyRelatedSeries() {
        return numberOfStudyRelatedSeries;
    }

    public void setNumberOfStudyRelatedSeries(String numberOfStudyRelatedSeries) {
        this.numberOfStudyRelatedSeries = numberOfStudyRelatedSeries;
    }

    public String getNumberOfStudyRelatedInstances() {
        return numberOfStudyRelatedInstances;
    }

    public void setNumberOfStudyRelatedInstances(String numberOfStudyRelatedInstances) {
        this.numberOfStudyRelatedInstances = numberOfStudyRelatedInstances;
    }
}

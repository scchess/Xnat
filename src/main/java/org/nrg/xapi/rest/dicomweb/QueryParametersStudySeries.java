package org.nrg.xapi.rest.dicomweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Translate parameter names between the DICOM and XNAT domains.
 *
 * This groups the Study and Series parameters. This could be done better without duplicating the Series code
 * using mixins but we don't have them yet.
 *
 * A DICOM query param can be repeated.
 *
 */
public class QueryParametersStudySeries extends QueryParameters {
    public static final String STUDY_INSTANCE_UID_NAME = "studyInstanceUID";
    public static final String STUDY_DATE_NAME = "studyDate";
    public static final String STUDY_TIME_NAME = "studyTime";
    public static final String MODALITIES_IN_STUDY_NAME = "modalitiesInStudy";
    public static final String REFERRING_PHYSICIAN_NAME_NAME = "referringPhysicianName";
    public static final String PATIENT_ID_NAME = "patientID";
    public static final String PATIENT_NAME_NAME = "patientName";
    public static final String ACCESSION_NUMBER_NAME = "accessionNumber";
    public static final String STUDY_ID_NAME = "studyID";

    public static final String MODALITY_NAME = "modality";
    public static final String SERIES_INSTANCE_UID_NAME = "seriesInstanceUID";
    public static final String SERIES_NUMBER_NAME = "seriesNumber";
    public static final String PERFORMED_PROCEDURE_STEP_STARTDATE = "PerformedProcedureStepStartDate";
    public static final String PERFORMED_PROCEDURE_STEP_STARTTIME = "PerformedProcedureStepStartTime";

    private static final Logger _log = LoggerFactory.getLogger(QueryParametersStudySeries.class);

    public QueryParametersStudySeries(Map<String, String> dicomRequestParams) {
        super(dicomRequestParams);
    }

    /**
     * Add the value of the DICOM-named parameter to the map.  It will be stored under its normalized name.
     *
     * @param dicomParamName
     * @param value
     */
    public void addDicomParameter(String dicomParamName, String value) {
        switch( dicomParamName.toLowerCase()) {
            case "studyinstanceuid":
            case "0020000d":
                List<String> uids = parseUIDs( value);
                for( String uid: uids) {
                    addParam( STUDY_INSTANCE_UID_NAME, uid);
                }
                break;
            case "studydate":
            case "00080020":
                addParam( STUDY_DATE_NAME, value);
                break;
            case "studytime":
            case "00080030":
                addParam( STUDY_TIME_NAME, value);
                break;
            case "modalitiesinstudy":
            case "00080061":
                addParam( MODALITIES_IN_STUDY_NAME, value);
                break;
            case "referringphysicianname":
            case "00080090":
                addParam( REFERRING_PHYSICIAN_NAME_NAME, value);
                break;
            case "00100020":
            case "patientid":
                addParam( PATIENT_ID_NAME, value);
                break;
            case "00100010":
            case "patientname":
                addParam( PATIENT_NAME_NAME, value);
                break;
            case "00080050":
            case "accessionnumber":
                addParam( ACCESSION_NUMBER_NAME, value);
                break;
            case "00200010":
            case "studyid":
                addParam( STUDY_ID_NAME, value);
                break;

            case "seriesinstanceuid":
            case "0020000E":
                uids = parseUIDs( value);
                for( String uid: uids) {
                    addParam( SERIES_INSTANCE_UID_NAME, uid);
                }
                break;
            case "modality":
            case "00080060":
                addParam( MODALITY_NAME, value);
                break;
            case "seriesnumber":
            case "00200011":
                addParam( SERIES_NUMBER_NAME, value);
                break;
            case "performedprocedurestepstartdate":
            case "00400244":
                addParam( PERFORMED_PROCEDURE_STEP_STARTDATE, value);
                break;
            case "performedprocedurestepstarttime":
            case "00400245":
                addParam( PERFORMED_PROCEDURE_STEP_STARTTIME, value);
                break;
            default:
                _log.warn("Ignoring unrecognized series/study-level query parameter: " + dicomParamName + " = " + value);
        }
    }

}

package org.nrg.xapi.rest.dicomweb;

import org.python.antlr.ast.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Translate parameter names between the DICOM and XNAT domains.
 *
 * A DICOM query param can be repeated.
 *
 */
public class QueryParametersStudy extends QueryParameters {
    public static final String STUDY_INSTANCE_UID_NAME = "studyInstanceUID";
    public static final String STUDY_DATE_NAME = "studyDate";
    public static final String STUDY_TIME_NAME = "studyTime";
    public static final String MODALITIES_IN_STUDY_NAME = "modalitiesInStudy";
    public static final String REFERRING_PHYSICIAN_NAME_NAME = "referringPhysicianName";
    public static final String PATIENT_ID_NAME = "patientID";
    public static final String PATIENT_NAME_NAME = "patientName";
    public static final String ACCESSION_NUMBER_NAME = "accessionNumber";
    public static final String STUDY_ID_NAME = "studyID";

    private static final Logger _log = LoggerFactory.getLogger(QueryParametersStudy.class);

    public QueryParametersStudy() {
        super();
    }

    public QueryParametersStudy(Map<String, String> dicomRequestParams) {
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
            default:
                _log.warn("Ignoring unrecognized study-level query parameter: " + dicomParamName + " = " + value);
        }
    }

}

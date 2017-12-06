package org.nrg.xapi.rest.dicomweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Translate parameter names between the DICOM and XNAT domains.
 *
 * A DICOM query param can be repeated.
 *
 */
public class QueryParametersSeriesWithStudyUID extends QueryParametersStudy {
    public static final String MODALITY_NAME = "modality";
    public static final String SERIES_INSTANCE_UID_NAME = "seriesInstanceUID";
    public static final String SERIES_NUMBER_NAME = "seriesNumber";
    public static final String PERFORMED_PROCEDURE_STEP_STARTDATE = "PerformedProcedureStepStartDate";
    public static final String PERFORMED_PROCEDURE_STEP_STARTTIME = "PerformedProcedureStepStartTime";

    private static final Logger _log = LoggerFactory.getLogger(QueryParametersSeriesWithStudyUID.class);

    public QueryParametersSeriesWithStudyUID() {
        super();
    }

    public QueryParametersSeriesWithStudyUID(Map<String, String> dicomRequestParams) {
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
            case "seriesinstanceuid":
            case "0020000E":
                List<String> uids = parseUIDs( value);
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
                _log.warn("Ignoring unrecognized series-level query parameter: " + dicomParamName + " = " + value);
        }
    }

}

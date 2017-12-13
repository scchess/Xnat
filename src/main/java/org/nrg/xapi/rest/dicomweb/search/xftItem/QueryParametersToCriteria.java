package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.rest.dicomweb.BaseQueryParameters;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.search.CriteriaCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryParametersToCriteria {

    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);

    public static CriteriaCollection mapSeries( String studyInstanceUID, BaseQueryParameters params) {
        CriteriaCollection cc = mapSeries( params);

        cc.addClause("xnat:imagesessiondata/uid", studyInstanceUID);
        return cc;
    }

    public static CriteriaCollection mapStudy(BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: params.keySet() ) {
            switch( paramName) {
                case QueryParameters.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParameters.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParameters.STUDY_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/study_id", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParameters.STUDY_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for( String uid: uids) {
                        cc_or_uid.addClause( "xnat:imagesessiondata/uid", "=", uid);
                    }
                    cc.addClause( cc_or_uid);
                    break;
                case QueryParameters.REFERRING_PHYSICIAN_NAME_NAME:
                    _log.warn("Study-level query parameter ReferringPhysicianName is not supported.");
                    break;
                case QueryParameters.PATIENT_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmpatientid", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParameters.PATIENT_NAME_NAME:
//                    cc.addClause( "xnat:imagesessionData/dcmpatientname", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parsePatientNameCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParameters.ACCESSION_NUMBER_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParameters.MODALITIES_IN_STUDY_NAME:
                    List<String> modalities = getModalities( params.getParams( paramName).get(0));

                    // Neither the straight AND or OR do the right thing.
//                    CriteriaCollection cc_and = new CriteriaCollection("AND");
//                    CriteriaCollection cc_or = new CriteriaCollection("OR");
//                    for( String modality: modalities) {
////                        cc_and.addClause( "xnat:imagescanData/modality", "=", modality);
//                        cc_or.addClause( "xnat:imagescanData/modality", "=", modality);
//                    }
////                    cc.addClause( cc_and);
//                    cc.addClause( cc_or);

                    // Filter on the first of the values, for now.
                    cc.addClause( "xnat:imagescanData/modality", "=", modalities.get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }

        return cc;
    }


    public static CriteriaCollection mapSeries( BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        for (String paramName : params.keySet()) {
            switch (paramName) {
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0)));
                    break;
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
                    break;
                case QueryParameters.SERIES_NUMBER_NAME:
                    cc.addClause("xnat:imagescanData/id", "=", params.getParams(paramName).get(0));
                    break;
                case QueryParameters.SERIES_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for (String uid : uids) {
                        cc_or_uid.addClause("xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause(cc_or_uid);
                    break;
                case QueryParameters.MODALITY_NAME:
                    cc.addClause("xnat:imagescanData/modality", "=", params.getParams(paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }
        return cc;
    }

    private static CriteriaCollection parseDateCriteria( String dateString) {
        return parseRangeCriteria( "xnat:experimentData/date", dateString);
    }

    private static CriteriaCollection parseTimeCriteria( String timeString) {
        return parseRangeCriteria( "xnat:experimentData/time", timeString);
    }

    private static CriteriaCollection parseRangeCriteria( String xmlPath, String value) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        if( value.contains("-")) {
            if( value.startsWith("-")) {
                cc.addClause( xmlPath, "<=" , value);
            }
            else if( value.endsWith("-")) {
                cc.addClause( xmlPath, ">=" , value);
            }
            else {
                String[] dates = value.split("-");
                cc.addClause( xmlPath, ">=" , dates[0]);
                cc.addClause( xmlPath, "<=" , dates[1]);
            }
        }
        else {
            cc.addClause( xmlPath, "=" , value);
        }
        return cc;
    }

    private static  CriteriaCollection parsePatientNameCriteria( String pName) {
        CriteriaCollection cc = new CriteriaCollection( "AND");
        if( pName.contains("*") || pName.contains("?")) {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "LIKE", pName.replaceAll("[\\*\\?]", "%"));
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "=", pName);
        }
        return cc;
    }

    private static List<String> getModalities( String modalitiesString) {
        List<String> modalities =  (modalitiesString != null)? parseMultivaluedValue( modalitiesString): new ArrayList<String>();
        return modalities;
    }

    private static List<String> parseMultivaluedValue(String value) {
        return Arrays.asList( value.split("\\\\"));
    }


}

package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xapi.rest.dicomweb.QueryParametersSeries;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudy;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudySeries;
import org.nrg.xft.search.CriteriaCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryParametersToCriteria {

    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);

    public static CriteriaCollection map( QueryParametersStudy params) {
        return mapStudy( params);
    }

    public static CriteriaCollection map( String studyInstanceUID, QueryParametersSeries params) {
        CriteriaCollection cc = mapSeries( params);

        cc.addClause("xnat:imagesessiondata/uid", studyInstanceUID);
        return cc;
    }

    private static CriteriaCollection mapStudy(QueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: params.keySet() ) {
            switch( paramName) {
                case QueryParametersStudy.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/study_id", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.STUDY_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for( String uid: uids) {
                        cc_or_uid.addClause( "xnat:imagesessiondata/uid", "=", uid);
                    }
                    cc.addClause( cc_or_uid);
                    break;
                case QueryParametersStudy.REFERRING_PHYSICIAN_NAME_NAME:
                    _log.warn("Study-level query parameter ReferringPhysicianName is not supported.");
                    break;
                case QueryParametersStudy.PATIENT_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmpatientid", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.PATIENT_NAME_NAME:
//                    cc.addClause( "xnat:imagesessionData/dcmpatientname", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parsePatientNameCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.ACCESSION_NUMBER_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.MODALITIES_IN_STUDY_NAME:
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


    private static CriteriaCollection mapSeries( QueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        for (String paramName : params.keySet()) {
            switch (paramName) {
                case QueryParametersSeries.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0)));
                    break;
                case QueryParametersSeries.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
                    break;
                case QueryParametersSeries.SERIES_NUMBER_NAME:
                    cc.addClause("xnat:imagescanData/id", "=", params.getParams(paramName).get(0));
                    break;
                case QueryParametersSeries.SERIES_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for (String uid : uids) {
                        cc_or_uid.addClause("xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause(cc_or_uid);
                    break;
                case QueryParametersSeries.MODALITY_NAME:
                    cc.addClause("xnat:imagescanData/modality", "=", params.getParams(paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }
        return cc;
    }

    public static CriteriaCollection map( QueryParametersStudySeries params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: params.keySet() ) {
            switch( paramName) {
                case QueryParametersStudy.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/study_id", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.STUDY_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for( String uid: uids) {
                        cc_or_uid.addClause( "xnat:imagesessiondata/uid", "=", uid);
                    }
                    cc.addClause( cc_or_uid);
                    break;
                case QueryParametersStudy.REFERRING_PHYSICIAN_NAME_NAME:
                    _log.warn("Study-level query parameter ReferringPhysicianName is not supported.");
                    break;
                case QueryParametersStudy.PATIENT_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmpatientid", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.PATIENT_NAME_NAME:
//                    cc.addClause( "xnat:imagesessionData/dcmpatientname", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parsePatientNameCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.ACCESSION_NUMBER_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "=" , params.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.MODALITIES_IN_STUDY_NAME:
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

                case QueryParametersSeries.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0)));
                    break;
                case QueryParametersSeries.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
                    break;
                case QueryParametersSeries.SERIES_NUMBER_NAME:
                    cc.addClause("xnat:imagescanData/id", "=", params.getParams(paramName).get(0));
                    break;
                case QueryParametersSeries.SERIES_INSTANCE_UID_NAME:
                    uids = params.getParams(paramName);
                    cc_or_uid = new CriteriaCollection("OR");
                    for (String uid : uids) {
                        cc_or_uid.addClause("xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause(cc_or_uid);
                    break;
                case QueryParametersSeries.MODALITY_NAME:
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

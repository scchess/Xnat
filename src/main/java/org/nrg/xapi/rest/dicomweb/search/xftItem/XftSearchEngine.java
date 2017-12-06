package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.model.dicomweb.*;
import org.nrg.xapi.rest.dicomweb.QueryParametersSeriesWithStudyUID;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudy;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatDcmcatalogBean;
import org.nrg.xdat.model.CatDcmentryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class XftSearchEngine implements SearchEngineI {

    private UserI user;
    private UserManagementServiceI userManagementService;
    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);

    @Autowired
    public XftSearchEngine(final UserManagementServiceI userManagementService) {

        this.userManagementService = userManagementService;
        // need to get the authenticated user here....
        try {
            this.user = userManagementService.getUser( "admin");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (UserInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DicomObjectI> getStudy(String studyInstanceUID) throws IOException {
        return null;
    }

    @Override
    public DicomObjectI[] getStudyAsArray(String studyInstanceUID) throws IOException {
        return new DicomObjectI[0];
    }

    @Override
    public List<? extends QIDOResponse> searchForStudies(QueryParametersStudy queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: queryParameters.keySet() ) {
            switch( paramName) {
                case QueryParametersStudy.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( queryParameters.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseTimeCriteria( queryParameters.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.STUDY_ID_NAME:
                    cc.addClause( "xnat:imagesessionData/study_id", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.STUDY_INSTANCE_UID_NAME:
                    List<String> uids = queryParameters.getParams(paramName);
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
                    cc.addClause( "xnat:imagesessionData/dcmpatientid", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.PATIENT_NAME_NAME:
//                    cc.addClause( "xnat:imagesessionData/dcmpatientname", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parsePatientNameCriteria( queryParameters.getParams( paramName).get(0)));
                    break;
                case QueryParametersStudy.ACCESSION_NUMBER_NAME:
                    cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                case QueryParametersStudy.MODALITIES_IN_STUDY_NAME:
                    List<String> modalities = queryParameters.getModalities();

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
                    _log.warn("Ignoring query parameter: " + queryParameters.asString(paramName));
                    break;
            }
        }

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

        QIDOStudyResponseList responses = new QIDOStudyResponseList();
//        List<QIDOResponse> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);
            QIDOResponseStudy response = new QIDOResponseStudy();
            response.setStudyDate( session.getExperimentdata().getDate());
            response.setStudyTime( session.getExperimentdata().getTime());
            response.setAccessionNumber( session.getDcmaccessionnumber());
            response.setInstanceAvailability( "ONLINE");
            response.setStudyInstanceUID( session.getUid());
            response.setPatientID( session.getDcmpatientid());
//            response.setPatientsName( session.getSubjectData().getLabel());
            response.setPatientsName( session.getDcmpatientname());
            response.setModalitiesInStudy( session.getModality());
            response.setPatientsSex( session.getSubjectData().getGender());
            response.setPatientsBirthDate( session.getSubjectData().getDOBDisplay());
            response.setStudyID( session.getStudyId());
            response.setNumberOfStudyRelatedSeries( countSeries( user, session));
            response.setNumberOfStudyRelatedInstances( countInstances( user, session));
            responses.add( response);
        }
        return responses;
    }

    @Override
    public List<? extends QIDOResponse> searchForSeries(String studyInstanceUID, QueryParametersSeriesWithStudyUID queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");

        cc.addClause( "xnat:imagesessiondata/uid", studyInstanceUID);

        for (String paramName: queryParameters.keySet() ) {
            switch( paramName) {
                case QueryParametersSeriesWithStudyUID.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause( parseRangeCriteria( "xnat:imagescandata/start_date", queryParameters.getParams( paramName).get(0)));
                    break;
                case QueryParametersSeriesWithStudyUID.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    cc.addClause( parseRangeCriteria( "xnat:imagescandata/starttime", queryParameters.getParams( paramName).get(0)));
                    break;
                case QueryParametersSeriesWithStudyUID.SERIES_NUMBER_NAME:
                    cc.addClause( "xnat:imagescanData/id", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                case QueryParametersSeriesWithStudyUID.SERIES_INSTANCE_UID_NAME:
                    List<String> uids = queryParameters.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for( String uid: uids) {
                        cc_or_uid.addClause( "xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause( cc_or_uid);
                    break;
                case QueryParametersSeriesWithStudyUID.MODALITY_NAME:
                    cc.addClause( "xnat:imagescanData/modality", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + queryParameters.asString(paramName));
                    break;
            }
        }

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);

//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponse> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagescandata scandata = new XnatImagescandata(item);
            QIDOResponseSeries response = new QIDOResponseSeries();
            response.setModality( scandata.getModality());
            response.setSeriesDescription( scandata.getSeriesDescription());
            response.setSeriesInstanceUID( scandata.getUid());
            response.setSeriesNumber( scandata.getId());
            response.setPerformedProcedureStepStartDate( scandata.getStartDate());
            response.setPerformedProcedureStepStartTime( scandata.getStarttime());
            response.setNumberOfSeriesRelatedInstances( scandata.getFrames());
            responses.add( response);
        }
        return responses;
    }

    private int countSeries( UserI user, XnatImagesessiondata session) {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);

        return imagescandata.size();
    }

    private int countInstances( UserI user, XnatImagesessiondata session) {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);

        int count = 0;
        for( XnatImagescandata scan: imagescandata) {
            count += scan.getFrames();
        }
        return count;
    }

    private CriteriaCollection parseDateCriteria( String dateString) {
        return parseRangeCriteria( "xnat:experimentData/date", dateString);
    }

    private CriteriaCollection parseTimeCriteria( String timeString) {
        return parseRangeCriteria( "xnat:experimentData/time", timeString);
    }

    private CriteriaCollection parseRangeCriteria( String xmlPath, String value) {
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

    private CriteriaCollection parsePatientNameCriteria( String pName) {
        CriteriaCollection cc = new CriteriaCollection( "AND");
        if( pName.contains("*") || pName.contains("?")) {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "LIKE", pName.replaceAll("[\\*\\?]", "%"));
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "=", pName);
        }
        return cc;
    }

    private XnatImagesessiondata getSession(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");
        cc.addClause( "xnat:imageSessionData/uid", "=" , studyInstanceUID);

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);


        XnatImagesessiondata session = new XnatImagesessiondata(ic.getFirst());
//        for( ItemI item: ic.getItems()) {
//            XnatImagesessiondata session = new XnatImagesessiondata(item);
//            QIDOResponse response = new QIDOResponse();
//            response.setStudyDate( session.getExperimentdata().getDate());
//            response.setStudyInstanceUID( session.getUid());
//            response.setPatientID( session.getSubjectId());
//            response.setPatientsName( session.getSubjectData().getLabel());
//            response.setModalitiesInStudy( session.getModality());
//            response.setPatientsSex( session.getSubjectData().getGender());
//            responses.add( response);
//        }
        return session;
    }

    private XnatImagescandata getScan( String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");
        cc.addClause( "xnat:imageSessionData/uid", "=" , studyInstanceUID);
        cc.addClause( "xnat:imageScanData/uid", "=" , seriesInstanceUID);

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);

        XnatImagescandata scan = null;
        if( ic.size() == 1) {
            scan = new XnatImagescandata( ic.get(0));
        }
        return scan;
    }

    private DicomObjectI getInstanceURL( String archiveRootPath, XnatImagescandata imageScanData, String sopInstanceUID) throws IOException {
        File file = null;
        for(XnatAbstractresourceI resourceI: imageScanData.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resourceI)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                if("RAW".equals( catResource.getContent()) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if(CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        CatDcmentryI dcmEntry = CatalogUtils.getDCMEntryByUID(dcmcatalog, sopInstanceUID);
                        file = CatalogUtils.getFile( dcmEntry, scanRootPath);
                        break;
                    }
                }
            }
        }
        return (file == null)? null: DicomObjectFactory.create( file);
    }

    @Override
    public DicomObjectI retrieveInstance(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception {

        XnatImagesessiondata session = getSession( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        DicomObjectI instance = getInstanceURL( session.getArchiveRootPath(), scan, sopInstanceUID);

        return instance;
    }

    public  List<String> getStudyUIDs(Map<String , String > params) throws Exception {
        List<String> studyUIDs = new ArrayList<>();
        CriteriaCollection cc = new CriteriaCollection("AND");
        cc.addClause( "xnat:experimentData/date", ">" , params.get("date"));
        for( String param: params.keySet()) {
            switch( param) {
                case QueryParametersStudy.STUDY_INSTANCE_UID_NAME:
                    cc.addClause( "xnat:experimentData/date", ">" , params.get( param));
                    default:
            }
        }
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

        return studyUIDs;
    }
}

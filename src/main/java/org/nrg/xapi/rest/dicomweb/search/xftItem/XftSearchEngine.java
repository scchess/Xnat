package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.model.dicomweb.*;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatDcmcatalogBean;
import org.nrg.xdat.model.CatDcmentryI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
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
import java.text.MessageFormat;
import java.util.*;

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
    public List<? extends QIDOResponse> searchForStudies(QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = QueryParametersToCriteria.mapStudy( queryParameters);

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
            response.setModalitiesInStudy( getModalitiesInStudy( session));
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
    public List<? extends QIDOResponse> searchForSeries(String studyInstanceUID, QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = QueryParametersToCriteria.mapSeries( studyInstanceUID, queryParameters);

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);

//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponse> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagescandata scandata = new XnatImagescandata(item);
            QIDOResponseSeries response = new QIDOResponseSeries();
            response.setModality( scandata.getModality());
            response.setSeriesDescription( scandata.getSeriesDescription());
            response.setSeriesInstanceUID( scandata.getUid());
            response.setSeriesNumber( (scandata.getSeriesNumber() != null)? scandata.getSeriesNumber().toString(): "");
            response.setPerformedProcedureStepStartDate( scandata.getStartDate());
            response.setPerformedProcedureStepStartTime( scandata.getStarttime());
            response.setNumberOfSeriesRelatedInstances( scandata.getInstanceCount());
            responses.add( response);
        }
        return responses;
    }

    @Override
    public List<? extends QIDOResponse> searchForStudySeries(QueryParameters queryParameters, UserI user) throws Exception {
        List<? extends QIDOResponse> responses = null;

        if( queryParameters.hasStudyLevel()) {
            List<? extends QIDOResponse> studyResponses = searchForStudySeriesByStudyParams( queryParameters, user);

            if( queryParameters.hasSeriesLevel()) {
                Iterator<? extends QIDOResponse> it = studyResponses.iterator();
                while( it.hasNext()) {
                    QIDOResponseStudySeries responseStudySeries = (QIDOResponseStudySeries) it.next();
                    String studyInstanceUID = responseStudySeries.getStudyInstanceUID();

                    responses = searchForStudySeriesByStudyUID( studyInstanceUID, queryParameters, user);
                }
            }
            else {
                responses = studyResponses;
            }
        }
        else if( queryParameters.hasSeriesLevel()) {
            responses = searchForStudySeriesBySeries( queryParameters, user);
        }
        else {
            responses = new ArrayList<>();
        }

        return responses;
    }

    @Override
    public DicomObjectI retrieveInstance(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception {

        XnatImagesessiondata session = getSession( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        DicomObjectI instance = getInstance( session.getArchiveRootPath(), scan, sopInstanceUID);

        return instance;
    }

    @Override
    public List<DicomObjectI> retrieveSeries(String studyInstanceUID, String seriesInstanceUID, UserI user) throws Exception {

        XnatImagesessiondata session = getSession( studyInstanceUID, null, null, user);
        XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, null, user);

        List<DicomObjectI> instances = getInstances( session.getArchiveRootPath(), scan);

        return instances;
    }

    @Override
    public List<DicomObjectI> retrieveStudy(String studyInstanceUID, UserI user) throws Exception {

        List<DicomObjectI> instances = new ArrayList<>();
        XnatImagesessiondata session = getSession( studyInstanceUID, null, null, user);
        for( XnatImagescandataI scan: session.getScans_scan()) {
            instances.addAll( getInstances( session.getArchiveRootPath(), scan));
        }

        return instances;
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudy( QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = QueryParametersToCriteria.mapStudy( queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    public List<? extends QIDOResponse> searchForStudySeriesBySeries( QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = QueryParametersToCriteria.mapSeries( queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudyUID(String studyInstanceUID, QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc ;
        if( studyInstanceUID != null) cc = QueryParametersToCriteria.mapSeries( studyInstanceUID, queryParameters);
        else  cc = QueryParametersToCriteria.mapSeries( queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudy( CriteriaCollection cc, UserI user) throws Exception {
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);

//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponseStudySeries> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagescandata scandata = new XnatImagescandata(item);
            QIDOResponseStudySeries response = new QIDOResponseStudySeries();
            response.setModality( scandata.getModality());
            response.setSeriesDescription( scandata.getSeriesDescription());
            response.setSeriesInstanceUID( scandata.getUid());
            response.setSeriesNumber( (scandata.getSeriesNumber() != null)? scandata.getSeriesNumber().toString(): "");
            response.setPerformedProcedureStepStartDate( scandata.getStartDate());
            response.setPerformedProcedureStepStartTime( scandata.getStarttime());
            response.setNumberOfSeriesRelatedInstances( scandata.getInstanceCount());

            response.setStudyDate( scandata.getImageSessionData().getExperimentdata().getDate());
            response.setStudyTime( scandata.getImageSessionData().getExperimentdata().getTime());
            response.setAccessionNumber( scandata.getImageSessionData().getDcmaccessionnumber());
            response.setInstanceAvailability( "ONLINE");
            response.setStudyInstanceUID( scandata.getImageSessionData().getUid());
            response.setPatientID( scandata.getImageSessionData().getDcmpatientid());
            response.setPatientsName( scandata.getImageSessionData().getDcmpatientname());
            response.setModalitiesInStudy( getModalitiesInStudy( scandata.getImageSessionData()));
            response.setPatientsSex( scandata.getImageSessionData().getSubjectData().getGender());
            response.setPatientsBirthDate( scandata.getImageSessionData().getSubjectData().getDOBDisplay());
            response.setStudyID( scandata.getImageSessionData().getStudyId());
            response.setNumberOfStudyRelatedSeries( countSeries( user, scandata.getImageSessionData()));
            response.setNumberOfStudyRelatedInstances( countInstances( user, scandata.getImageSessionData()));

            responses.add( response);
        }
        return responses;
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudyParams( QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = QueryParametersToCriteria.mapStudy( queryParameters);
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponseStudySeries> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);

            List<XnatImagescandataI> scans = session.getScans_scan();
            for( XnatImagescandataI scan: scans) {
                QIDOResponseStudySeries response = new QIDOResponseStudySeries();
                response.setModality(scan.getModality());
                response.setSeriesDescription(scan.getSeriesDescription());
                response.setSeriesInstanceUID(scan.getUid());
                response.setSeriesNumber((scan.getSeriesNumber() != null)? scan.getSeriesNumber().toString(): "");
                response.setPerformedProcedureStepStartDate(scan.getStartDate());
                response.setPerformedProcedureStepStartTime(scan.getStarttime());
                response.setNumberOfSeriesRelatedInstances(scan.getInstanceCount());

                response.setStudyDate(session.getExperimentdata().getDate());
                response.setStudyTime(session.getExperimentdata().getTime());
                response.setAccessionNumber(session.getDcmaccessionnumber());
                response.setInstanceAvailability("ONLINE");
                response.setStudyInstanceUID(session.getUid());
                response.setPatientID(session.getDcmpatientid());
                response.setPatientsName(session.getDcmpatientname());
                response.setModalitiesInStudy(getModalitiesInStudy(session));
                response.setPatientsSex(session.getSubjectData().getGender());
                response.setPatientsBirthDate(session.getSubjectData().getDOBDisplay());
                response.setStudyID(session.getStudyId());
                response.setNumberOfStudyRelatedSeries(countSeries(user, session));
                response.setNumberOfStudyRelatedInstances(countInstances(user, session));

                responses.add(response);
            }
        }
        return responses;
    }

    private String getModalitiesInStudy( XnatImagesessiondata session) {
        SortedSet<String> modalitySet = new TreeSet<>();
        for( XnatImagescandataI scan: session.getScans_scan()) {
            modalitySet.add( scan.getModality());
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = modalitySet.iterator();
        while( it.hasNext()) {
            sb.append( it.next());
            if( it.hasNext()) sb.append("\\");
        }
        return sb.toString();
    }

    private int countSeries( UserI user, XnatImagesessiondata session) {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);

        return imagescandata.size();
    }

    /**
     * Returns the sum of instances in each scan in the provided session in which the user has access.
     *
     * @param user
     * @param session
     * @return number of instances in the session to which the user has access.
     * @throws RuntimeException if a scan does not have a valid instance count database entry.
     */
    private int countInstances( UserI user, XnatImagesessiondata session) {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);

        int count = 0;
        for( XnatImagescandata scan: imagescandata) {
            Integer n = scan.getInstanceCount();
            if( n == null) {
                String msg = MessageFormat.format("Scan {0} in session {1} does not have instance count.", scan.getId(), session.getId());
                throw new RuntimeException(msg);
            }
            count += n;
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
        if( ic.size() > 0) {
            scan = new XnatImagescandata( ic.get(0));
        }
        if( ic.size() > 1) {
            _log.warn("Multiple scans (" + ic.size() + ") with study uid: " + studyInstanceUID + ", series uid: " + seriesInstanceUID);
        }
        return scan;
    }

    private DicomObjectI getInstance( String archiveRootPath, XnatImagescandata imageScanData, String sopInstanceUID) throws IOException {
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

    private List<DicomObjectI> getInstances( String archiveRootPath, XnatImagescandataI imageScanData) throws IOException {
        List<DicomObjectI> instances = new ArrayList<>();
        for(XnatAbstractresourceI resourceI: imageScanData.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resourceI)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                if("RAW".equals( catResource.getContent()) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if(CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        for(CatEntryI entry: CatalogUtils.getEntriesByFilter(dcmcatalog, new CatalogUtils.CatEntryFilterI() {
                            @Override
                            public boolean accept(CatEntryI entry) {
                                return true;
                            }
                        })) {
                            CatDcmentryI dcmentry = (CatDcmentryI) entry;
                            File file = CatalogUtils.getFile( dcmentry, scanRootPath);
                            if( file != null) {
                                instances.add( DicomObjectFactory.create(file));
                            }
                        }
                    }
                }
            }
        }
        return instances;
    }

    public  List<String> getStudyUIDs(Map<String , String > params) throws Exception {
        List<String> studyUIDs = new ArrayList<>();
        CriteriaCollection cc = new CriteriaCollection("AND");
        cc.addClause( "xnat:experimentData/date", ">" , params.get("date"));
        for( String param: params.keySet()) {
            switch( param) {
                case QueryParameters.STUDY_INSTANCE_UID_NAME:
                    cc.addClause( "xnat:experimentData/date", ">" , params.get( param));
                    default:
            }
        }
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

        return studyUIDs;
    }
}

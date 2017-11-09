package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.io.xnat.XnatCatalogURIIterator;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatDcmcatalogBean;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.CatDcmentryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.*;
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
    public List<QIDOResponse> searchForStudies(QueryParameters queryParameters, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: queryParameters.keySet() ) {
            switch( paramName) {
                case QueryParameters.STUDY_DATE_NAME:
                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + queryParameters.asString(paramName));
                    break;
            }
        }

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

        List<QIDOResponse> responses = new ArrayList<>();
        for( ItemI item: ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);
            QIDOResponse response = new QIDOResponse();
            response.setStudyDate( session.getExperimentdata().getDate());
            response.setStudyInstanceUID( session.getUid());
            response.setPatientID( session.getSubjectId());
            response.setPatientsName( session.getSubjectData().getLabel());
            response.setModalitiesInStudy( session.getModality());
            response.setPatientsSex( session.getSubjectData().getGender());
            responses.add( response);
        }
        return responses;
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
                case QueryParameters.STUDY_INSTANCE_UID_NAME:
                    cc.addClause( "xnat:experimentData/date", ">" , params.get( param));
                    default:
            }
        }
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);

        return studyUIDs;
    }
}

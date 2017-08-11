package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

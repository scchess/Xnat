/*
 * web: org.nrg.xapi.rest.dicom.AnonymizeApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.dicomweb;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.ProjectId;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.merge.AnonUtils;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.nrg.xdat.security.helpers.AccessLevel.*;

@Api(description = "XNAT DICOM Web API")
@XapiRestController
@RequestMapping(value = "/dicomweb")
public class DicomWebApi extends AbstractXapiProjectRestController {

    private final JdbcTemplate _template;

    @Autowired
    public DicomWebApi(final JdbcTemplate template, final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final AnonUtils anonUtils, final SiteConfigPreferences preferences) {
        super(userManagementService, roleHolder);
        _preferences = preferences;
        _template = template;
    }

//    @ApiOperation(value = "QIDO-RS SearchForStudies.", response = QIDOResponse.class)
//    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
//            @ApiResponse(code = 204, message = "The specified subject was found but had no associated experiments."),
//            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
//            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
//    @XapiRequestMapping(value = "studies", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
//    @ResponseBody
//    public ResponseEntity<List<QIDOResponse>> doSearchForStudies( @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException, NoContentException {
//        Set<String> paramNames = allRequestParams.keySet();
//
//        if (paramNames == null || paramNames.isEmpty()) {
//            // badly formatted query. No query params specified.
//        }
//
//        if (paramNames.size() > 1) {
//            // only one supported to start
//        }
//
//        String[] s = new String[paramNames.size()];
//        List<QIDOResponse> response = null;
//        paramNames.toArray(s);
//        UserI user = getSessionUser();
//        switch (s[0]) {
//            case "00100020":
//                response = getStudiesBySubject2(allRequestParams.get("00100020"), user);
//                break;
//            case "PatientID":
//                response = getStudiesBySubject2(allRequestParams.get("PatientID"), user);
//                break;
//            default:
//                // unsupported query param.
//                break;
//        }
//        return new ResponseEntity<List<QIDOResponse>>(response, HttpStatus.OK );
//    }

    @ApiOperation(value = "QIDO-RS SearchForStudies.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "The specified subject was found but had no associated experiments."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<QIDOResponse>> doSearchForStudies( @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException, NoContentException {
        Set<String> paramNames = allRequestParams.keySet();

        if (paramNames == null || paramNames.isEmpty()) {
            // badly formatted query. No query params specified.
        }

        final DicomWebStudyViewSelect statement = new DicomWebStudyViewSelect();

        for( String paramName: allRequestParams.keySet()) {
            switch( paramName) {
                case "00100020":
                case "PatientID":
                    statement.addAndClause( statement.getPatientIDLabel(), allRequestParams.get(paramName));
                    break;
                case "00100010":
                case "PatientName":
                    statement.addAndClause( statement.getPatientNameLabel(), allRequestParams.get(paramName));
                    break;
                case "0020000D":
                case "StudyInstanceUID":
                    List<String> uids = parseUIDs( allRequestParams.get(paramName));
                    for( String uid: uids) {
                        statement.addOrClause( statement.getStudyInstanceUIDLabel(), uid);
                    }
                    break;
                default:
                    // unsupported query param.
                    break;
            }
        }

        List<QIDOResponse> qidoResponses = _template.query(statement.getStatement(), new RowMapper<QIDOResponse>() {

            @Override
            public QIDOResponse mapRow(ResultSet resultSet, int i) throws SQLException {
                QIDOResponse response = new QIDOResponse();
                response.setPatientsName( resultSet.getString(statement.getPatientIDLabel()));
                response.setStudyInstanceUID( resultSet.getString(statement.getStudyInstanceUIDLabel()));
                response.setAccessionNumber( resultSet.getString(statement.getAccessionNumberLabel()));
                response.setModalitiesInStudy( resultSet.getString(statement.getModalitiesLabel()));
//                response.setPatientsSex( resultSet.getString(statement.getPatientSexLabel()));
                return response;
            }
        });

        return new ResponseEntity<List<QIDOResponse>>(qidoResponses, HttpStatus.OK );
    }

    public List<String> parseUIDs( String uids) {
        return Arrays.asList( uids.split("\\\\"));
    }

    private QIDOResponse getStudiesBySubject( String subjectId) {
        XnatSubjectdata subject = null;
        QIDOResponse qidoResponse = new QIDOResponse();
        XnatProjectdata proj=null;

        String projectId = "testproject1";
        UserI user = getSessionUser();

        proj = XnatProjectdata.getProjectByIDorAlias( projectId, user, false);

        // subject
        subject = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);

        if (subject == null) {
            subject = XnatSubjectdata.getXnatSubjectdatasById(subject, user, false);
            if (subject != null && (proj != null && !subject.hasProject(proj.getId()))) {
                subject = null;
            }
        }

        String sessionId = "XNAT_E00001";
        XnatImagesessiondata session = XnatImagesessiondata.getXnatImagesessiondatasById(sessionId, user, false);

        String studyUID = session.getUid();

        String scanId = "3000897";
        List<XnatImagescandata> scans = XnatImagescandata.getScansByIdORType(scanId, session, user, false );

        for( XnatImagescandata scan: scans) {

        }
        XnatImagescandata scan = scans.get(0);
        String seriesUID = scan.getUid();

        qidoResponse.setStudyInstanceUID( studyUID);
        qidoResponse.setAccessionNumber( session.getDcmaccessionnumber());
        qidoResponse.setModalitiesInStudy( session.getModality());
        qidoResponse.setPatientsSex( subject.getGender());
        qidoResponse.setPatientsName( session.getDcmpatientname());

        return qidoResponse;
    }

    private List<QIDOResponse> getStudiesBySubject2( String subjectId, UserI user) {
        List<QIDOResponse> responses = new ArrayList<>();
        XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById( subjectId, user, false);

        List<XnatSubjectassessordata> accessors = subject.getExperiments_experiment( "xnat:imageSessionData");

        for( XnatSubjectassessordata accessor: accessors) {
            if( accessor instanceof XnatImagesessiondata) {
                XnatImagesessiondata session = (XnatImagesessiondata) accessor;

                QIDOResponse qidoResponse = new QIDOResponse();
                qidoResponse.setStudyInstanceUID( session.getUid());
                qidoResponse.setAccessionNumber( session.getDcmaccessionnumber());
                qidoResponse.setModalitiesInStudy( session.getModality());
                qidoResponse.setPatientsSex( subject.getGender());
                qidoResponse.setPatientsName( session.getDcmpatientname());

                responses.add( qidoResponse);
            }
        }

        return responses;
    }

    @ApiOperation(value = "Gets the subject's experiements.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the subject's experiments."),
            @ApiResponse(code = 204, message = "The specified subject was found but had no associated experiments."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the subject's experiments."),
            @ApiResponse(code = 404, message = "The specified subject wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subject/{subjectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<XnatSubjectdata> getExptsBySubject(@PathVariable("subjectId") final String subjectId) throws NrgServiceException, NoContentException {
        XnatProjectdata proj=null;
        XnatSubjectdata subject=null;

        String projectId = "testproject1";
        UserI user = getSessionUser();

        proj = XnatProjectdata.getProjectByIDorAlias( projectId, user, false);

        // subject
        subject = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);

        if (subject == null) {
            subject = XnatSubjectdata.getXnatSubjectdatasById(subject, user, false);
            if (subject != null && (proj != null && !subject.hasProject(proj.getId()))) {
                subject = null;
            }
        }

        String sessionId = "XNAT_E00001";
        XnatImagesessiondata session = XnatImagesessiondata.getXnatImagesessiondatasById(sessionId, user, false);

        String studyUID = session.getUid();

        String scanId = "3000897";
        List<XnatImagescandata> scans = XnatImagescandata.getScansByIdORType(scanId, session, user, false );

        XnatImagescandata scan = scans.get(0);
        String seriesUID = scan.getUid();

        return new ResponseEntity<>( subject, HttpStatus.OK);
    }


    @ApiOperation(value = "Sets the site-wide anonymization script.", response = Void.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully stored the contents of the site-wide anonymization script."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to modify the site-wide anonymization script."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "site", consumes = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.PUT, restrictTo = Admin)
    public ResponseEntity<Void> setSiteWideAnonScript(@RequestBody final String script) throws ConfigServiceException {
        _preferences.setSitewideAnonymizationScript(script);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private static final Logger _log = LoggerFactory.getLogger(DicomWebApi.class);

    private final SiteConfigPreferences _preferences;
}

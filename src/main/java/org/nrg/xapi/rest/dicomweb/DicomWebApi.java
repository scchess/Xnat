/*
 * web: org.nrg.xapi.rest.dicom.AnonymizeApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.dicomweb;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nrg.xdat.security.helpers.AccessLevel.Read;

@Api(description = "XNAT DICOM Web API")
@XapiRestController
@RequestMapping(value = "/dicomweb")
public class DicomWebApi extends AbstractXapiProjectRestController {

    private final SearchEngineI _searchEngine;
    private final SiteConfigPreferences _preferences;
    private static final Logger _log = LoggerFactory.getLogger(DicomWebApi.class);


    @Autowired
    public DicomWebApi( final UserManagementServiceI userManagementService, final RoleHolder roleHolder, SearchEngineI searchEngine, final SiteConfigPreferences preferences) {
        super(userManagementService, roleHolder);
        _searchEngine = searchEngine;
        _preferences = preferences;
    }

    @ApiOperation(value = "QIDO-RS SearchForStudies.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "The specified subject was found but had no associated experiments."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<QIDOResponse>> doSearchForStudies( @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException, NoContentException {
        Set<String> paramNames = allRequestParams.keySet();

        if (paramNames == null || paramNames.isEmpty()) {
            // badly formatted query. No query params specified.
        }

        UserI user = getSessionUser();

        QueryParametersStudy dicomQueryParams = new QueryParametersStudy( allRequestParams);
        List<QIDOResponse> qidoResponses = null;
        try {
            qidoResponses = _searchEngine.searchForStudies( dicomQueryParams, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<QIDOResponse>>(qidoResponses, HttpStatus.OK );
    }


    @ApiOperation(value = "WADO-RS Retrieve Instance.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}", produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveInstance( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                  @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                  @PathVariable("sopInstanceUID") String sopInstanceUID) throws NrgServiceException, NoContentException {
        UserI user = getSessionUser();

        DicomObjectI instance = null;
        List<DicomObjectI> instances = new ArrayList<>();
        try {
            instance = _searchEngine.retrieveInstance( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
            instances.add(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(instances, HttpStatus.OK );
    }


    @ApiOperation(value = "Gets the specified DICOM study.", response = DicomObjectI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the study."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the study."),
            @ApiResponse(code = 404, message = "The specified study wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/", produces = "multipart/related", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BodyPart<DicomObjectI>>> retrieveStudy( @PathVariable("studyInstanceUID") String studyInstanceUID) throws NrgServiceException, NoContentException {

        try {
            DicomObjectI[] dicomObjects = _searchEngine.getStudyAsArray(studyInstanceUID);
            MediaType mediaType = new MediaType("application", "dicom");
            List<BodyPart<DicomObjectI>> bodyParts = new ArrayList<>(dicomObjects.length);
            for( DicomObjectI dobj: dicomObjects) {
                BodyPart<DicomObjectI> bp = new BodyPart<>(dobj, mediaType);
                bodyParts.add( bp);
            }
            return new ResponseEntity<>( bodyParts, HttpStatus.OK);
        }
        catch( IOException e) {
            // throw an internal server error here.
        }

        return null;
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

}

package org.nrg.xapi.rest.dicomweb.search;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.security.UserI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SearchEngineI {

    List<DicomObjectI> getStudy(String studyInstanceUID) throws IOException;

    DicomObjectI[] getStudyAsArray(String studyInstanceUID) throws IOException;

    List<QIDOResponse> searchForStudies(QueryParameters queryParameters, UserI user) throws Exception;

    DicomObjectI retrieveInstance(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception;
}

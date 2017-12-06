package org.nrg.xapi.rest.dicomweb.search;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.model.dicomweb.QIDOResponseStudy;
import org.nrg.xapi.rest.dicomweb.QueryParametersSeriesWithStudyUID;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudy;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.util.List;

public interface SearchEngineI {

    List<DicomObjectI> getStudy(String studyInstanceUID) throws IOException;

    DicomObjectI[] getStudyAsArray(String studyInstanceUID) throws IOException;

    List<? extends QIDOResponse> searchForStudies(QueryParametersStudy queryParameters, UserI user) throws Exception;

    List<? extends QIDOResponse> searchForSeries(String studyInstanceUID, QueryParametersSeriesWithStudyUID queryParameters, UserI user) throws Exception;

    DicomObjectI retrieveInstance(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception;
}

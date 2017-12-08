package org.nrg.xapi.rest.dicomweb.search.jdbc;

import org.h2.util.StringUtils;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParametersSeries;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudy;
import org.nrg.xapi.rest.dicomweb.QueryParametersStudySeries;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//@Component
public class JdbcSearchEngine implements SearchEngineI {

    private final JdbcTemplate _template;
    private static final Logger _log = LoggerFactory.getLogger(JdbcSearchEngine.class);
    private final SiteConfigPreferences _preferences;

    private UserI user;

    @Autowired
    public JdbcSearchEngine(final JdbcTemplate template, final SiteConfigPreferences preferences) {
        _preferences = preferences;
        _template = template;
    }

    @Override
    public List<DicomObjectI> getStudy(String studyInstanceUID) throws IOException {
        // TODO:  user access enforced.  IF not loop over each object and check.
        ArrayList<XnatImagesessiondata> imagesessiondatas = XnatImagesessiondata.getXnatImagesessiondatasByField("xnat:imageSessionData/UID", studyInstanceUID, user, false);

        // If matches more than one.  wtf?
        if (imagesessiondatas.size() > 1) {
            throw new IOException("Multiple instances match same studyInstance: " + studyInstanceUID);
        } else if (imagesessiondatas.isEmpty()) {
            // return 404
        }

        List<DicomObjectI> dcmFiles = new ArrayList<>();
        XnatImagesessiondata isd = imagesessiondatas.get(0);
        for (XnatImagescandataI imagescandata : isd.getScans_scan()) {
            for (XnatAbstractresourceI resourceI : imagescandata.getFile()) {
                if (StringUtils.equals("DICOM", resourceI.getLabel()) && resourceI instanceof XnatResourcecatalogI) {
                    XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                    File catFile = null;
                    try {
                        catFile = CatalogUtils.getCatalogFile(isd.getArchiveRootPath(), catResource);
                    } catch (BaseXnatExperimentdata.UnknownPrimaryProjectException e) {
// throw 500?
                    }
                    CatCatalogBean catalog = CatalogUtils.getCatalog(catFile);
                    if (catalog.getEntries_entry().size() > 0) {
                        File file = CatalogUtils.getFile(catalog.getEntries_entry().get(0), catFile.getParentFile().getAbsolutePath());
                        dcmFiles.add(DicomObjectFactory.create(file));
                    }
                }
            }
        }

//        List<DicomObjectI> dcmFiles = new ArrayList<>();
//        dcmFiles.add(DicomObjectFactory.create( new File("/data/xnat/archive/testproject1/arc001/Cucumber_MR1/SCANS/601/DICOM/1.3.46.670589.11.5730.5.0.1268.2010042909472232027-601-1-1apb4sk.dcm")));
//        dcmFiles.add(DicomObjectFactory.create( new File("/data/xnat/archive/testproject1/arc001/Cucumber_MR1/SCANS/601/DICOM/1.3.46.670589.11.5730.5.0.1268.2010042909472232027-601-1-1apb4sk.dcm")));
        return dcmFiles;
    }

    @Override
    public DicomObjectI[] getStudyAsArray(String studyInstanceUID) throws IOException {
        List<DicomObjectI> dcmFiles = new ArrayList<>();
        dcmFiles.add(DicomObjectFactory.create(new File("/data/xnat/archive/testproject1/arc001/Cucumber_MR1/SCANS/601/DICOM/1.3.46.670589.11.5730.5.0.1268.2010042909472232027-601-1-1apb4sk.dcm")));
        dcmFiles.add(DicomObjectFactory.create(new File("/data/xnat/archive/testproject1/arc001/Cucumber_MR1/SCANS/601/DICOM/1.3.46.670589.11.5730.5.0.1268.2010042909472232027-601-1-1apb4sk.dcm")));
        DicomObjectI[] d = new DicomObjectI[dcmFiles.size()];
        return dcmFiles.toArray(d);
    }


    @Override
    public List<QIDOResponse> searchForStudies(QueryParametersStudy queryParameters, UserI user) {
        final DicomWebStudyViewSelect statement = new DicomWebStudyViewSelect();

        for (String paramName : queryParameters.keySet()) {
            switch (paramName) {
                case QueryParametersStudy.PATIENT_ID_NAME:
                    statement.addAndClause(statement.getPatientIDLabel(), queryParameters.getParams( QueryParametersStudy.PATIENT_ID_NAME).get(0));
                    break;
                case QueryParametersStudy.PATIENT_NAME_NAME:
                    statement.addAndClause(statement.getPatientNameLabel(), queryParameters.getParams( QueryParametersStudy.PATIENT_NAME_NAME).get(0));
                    break;
                case QueryParametersStudy.ACCESSION_NUMBER_NAME:
                    statement.addAndClause(statement.getAccessionNumberLabel(), queryParameters.getParams( QueryParametersStudy.PATIENT_NAME_NAME).get(0));
                    break;
                case QueryParametersStudy.STUDY_INSTANCE_UID_NAME:
                    List<String> uids = queryParameters.getParams( QueryParametersStudy.STUDY_INSTANCE_UID_NAME);
                    for (String uid : uids) {
                        statement.addOrClause(statement.getStudyInstanceUIDLabel(), uid);
                    }
                    break;
                default:
                    _log.warn("Ignoring query parameter " + paramName + " with value(s) " + queryParameters.getParamsString( paramName));
                    break;
            }
        }

        List<QIDOResponse> qidoResponses = _template.query(statement.getStatement(), new RowMapper<QIDOResponse>() {

            @Override
            public QIDOResponse mapRow(ResultSet resultSet, int i) throws SQLException {
                QIDOResponse response = new QIDOResponse();
//                response.setPatientsName(resultSet.getString(statement.getPatientIDLabel()));
//                response.setStudyInstanceUID(resultSet.getString(statement.getStudyInstanceUIDLabel()));
//                response.setAccessionNumber(resultSet.getString(statement.getAccessionNumberLabel()));
//                response.setModalitiesInStudy(resultSet.getString(statement.getModalitiesLabel()));
//                response.setPatientsSex( resultSet.getString(statement.getPatientSexLabel()));
                return response;
            }
        });

        return qidoResponses;
    }

    @Override
    public List<QIDOResponse> searchForSeries(String studyInstanceUID, QueryParametersSeries queryParameters, UserI user) throws Exception {
        return null;
    }

    @Override
    public List<? extends QIDOResponse> searchForSeries(QueryParametersStudySeries queryParameters, UserI user) throws Exception {
        return null;
    }

    @Override
    public DicomObjectI retrieveInstance(String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) {
        return null;
    }
}


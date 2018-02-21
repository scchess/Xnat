package org.nrg.xnat.archive.operations;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dcm4che2.data.*;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.util.TagUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.config.entities.Configuration;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.dicomtools.filters.SeriesImportFilter;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.Files;
import org.nrg.xnat.Labels;
import org.nrg.xnat.archive.GradualDicomImporter;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.archive.processors.MizerArchiveProcessor;
import org.nrg.xnat.helpers.prearchive.DatabaseSession;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.nrg.xnat.archive.GradualDicomImporter.SENDER_AE_TITLE_PARAM;
import static org.nrg.xnat.archive.GradualDicomImporter.SENDER_ID_PARAM;

@Slf4j
public class GradualDicomImportOperation extends AbstractDicomImportOperation {

    public GradualDicomImportOperation(final Object control,
                                       final UserI user,
                                       final FileWriterWrapperI fileWriter,
                                       final Map<String, Object> parameters,
                                       final List<ArchiveProcessor> processors,
                                       final DicomFilterService filterService,
                                       final DicomObjectIdentifier<XnatProjectdata> identifier,
                                       final MizerService mizer,
                                       final DicomFileNamer namer) {
        super(control, user, parameters, fileWriter, identifier, namer, mizer, filterService, processors);

        if (getParameters().containsKey(GradualDicomImporter.TSUID_PARAM)) {
            _transferSyntax = TransferSyntax.valueOf((String) getParameters().get(GradualDicomImporter.TSUID_PARAM));
        } else {
            _transferSyntax = null;
        }
    }

    @Override
    public List<String> call() throws Exception {
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(getFileWriter().getInputStream());
             final DicomInputStream dicomInputStream = null == getTransferSyntax() ? new DicomInputStream(bufferedInputStream) : new DicomInputStream(bufferedInputStream, getTransferSyntax())) {
            return processDicomInputStream(bufferedInputStream, dicomInputStream);
        } catch (ClientException e) {
            throw e;
        } catch (Throwable t) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST, "unable to read DICOM object " + getFileWriter().getName(), t);
        }
    }

    protected List<String> processDicomInputStream(final InputStream inputStream, final DicomInputStream dicomInputStream) throws ClientException, IOException, ServerException {
        final String                                 name                  = getFileWriter().getName();
        final DicomObject                            dicom;
        final XnatProjectdata                        project;
        final DicomObjectIdentifier<XnatProjectdata> dicomObjectIdentifier = getIdentifier();

        final int lastTag = Math.max(dicomObjectIdentifier.getTags().last(), Tag.SeriesDescription) + 1;
        log.trace("reading object into memory up to {}", TagUtils.toString(lastTag));
        dicomInputStream.setHandler(new StopTagInputHandler(lastTag));
        dicom = dicomInputStream.readDicomObject();

        log.trace("handling file with query parameters {}", getParameters());
        try {
            // project identifier is expensive, so avoid if possible
            project = getProject(PrearcUtils.identifyProject(getParameters()),
                                 new Callable<XnatProjectdata>() {
                                     public XnatProjectdata call() {
                                         return dicomObjectIdentifier.getProject(dicom);
                                     }
                                 });
        } catch (MalformedURLException e1) {
            log.error("unable to parse supplied destination flag", e1);
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST, e1);
        }
        final String             projectId     = project != null ? (String) project.getProps().get("id") : null;
        final SeriesImportFilter siteFilter    = getFilterService().getSeriesImportFilter();
        final SeriesImportFilter projectFilter = StringUtils.isNotBlank(projectId) ? getFilterService().getSeriesImportFilter(projectId) : null;
        if (log.isDebugEnabled()) {
            if (siteFilter != null) {
                if (projectFilter != null) {
                    log.debug("Found " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " site-wide series import filter and " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " series import filter for the project " + projectId);
                } else if (StringUtils.isNotBlank(projectId)) {
                    log.debug("Found " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " site-wide series import filter and no series import filter for the project " + projectId);
                } else {
                    log.debug("Found a site-wide series import filter and no project ID was specified");
                }
            } else if (projectFilter != null) {
                log.debug("Found no site-wide series import filter and " + (projectFilter.isEnabled() ? "enabled" : "disabled") + " series import filter for the project " + projectId);
            }
        }
        if (!(shouldIncludeDicomObject(siteFilter, dicom) && shouldIncludeDicomObject(projectFilter, dicom))) {
            return Collections.emptyList();
            /* TODO: Return information to user on rejected files. Unfortunately throwing an
             * exception causes DicomBrowser to display a panicked error message. Some way of
             * returning the information that a particular file type was not accepted would be
             * nice, though. Possibly record the information and display on an admin page.
             * Work to be done for 1.7
             */
        }
        try {
            inputStream.reset();
        } catch (IOException e) {
            log.error("unable to reset DICOM data stream", e);
        }
        if (Strings.isNullOrEmpty(dicom.getString(Tag.SOPClassUID))) {
            throw new ClientException("object " + name + " contains no SOP Class UID");
        }
        if (Strings.isNullOrEmpty(dicom.getString(Tag.SOPInstanceUID))) {
            throw new ClientException("object " + name + " contains no SOP Instance UID");
        }

        final String studyInstanceUID = dicom.getString(Tag.StudyInstanceUID);
        log.trace("Looking for study {} in project {}", studyInstanceUID, null == project ? null : project.getId());

        // Fill a SessionData object in case it is the first upload
        final File root;
        if (null == project) {
            root = new File(ArcSpecManager.GetInstance().getGlobalPrearchivePath());
        } else {
            //root = new File(project.getPrearchivePath());
            root = new File(ArcSpecManager.GetInstance().getGlobalPrearchivePath() + "/" + project.getId());
        }

        final String sessionLabel;
        if (getParameters().containsKey(URIManager.EXPT_LABEL)) {
            sessionLabel = (String) getParameters().get(URIManager.EXPT_LABEL);
            log.trace("using provided experiment label {}", getParameters().get(URIManager.EXPT_LABEL));
        } else {
            sessionLabel = StringUtils.defaultIfBlank(dicomObjectIdentifier.getSessionLabel(dicom), "dicom_upload");
        }

        final String visit;
        if (getParameters().containsKey(URIManager.VISIT_LABEL)) {
            visit = (String) getParameters().get(URIManager.VISIT_LABEL);
            log.trace("using provided visit label {}", getParameters().get(URIManager.VISIT_LABEL));
        } else {
            visit = null;
        }

        final String subject;
        if (getParameters().containsKey(URIManager.SUBJECT_ID)) {
            subject = (String) getParameters().get(URIManager.SUBJECT_ID);
        } else {
            subject = dicomObjectIdentifier.getSubjectLabel(dicom);
        }

        final File timestamp = new File(root, PrearcUtils.makeTimestamp());

        if (null == subject) {
            log.trace("subject is null for session {}/{}", timestamp, sessionLabel);
        }

        // Query the cache for an existing session that has this Study Instance UID, project name, and optional modality.
        // If found the SessionData object we just created is over-ridden with the values from the cache.
        // Additionally a record of which operation was performed is contained in the Either<SessionData,SessionData>
        // object returned.
        //
        // This record is necessary so that, if this row was created by this call, it can be deleted if anonymization
        // goes wrong. In case of any other error the file is left on the filesystem.
        final PrearcDatabase.Either<SessionData, SessionData> getOrCreate;
        final SessionData                                     session;
        try {
            final SessionData initialize = new SessionData();
            initialize.setFolderName(sessionLabel);
            initialize.setName(sessionLabel);
            initialize.setProject(project == null ? null : project.getId());
            initialize.setVisit(visit);
            initialize.setScan_date(dicom.getDate(Tag.StudyDate));
            initialize.setTag(studyInstanceUID);
            initialize.setTimestamp(timestamp.getName());
            initialize.setStatus(PrearcUtils.PrearcStatus.RECEIVING);
            initialize.setLastBuiltDate(Calendar.getInstance().getTime());
            initialize.setSubject(subject);
            initialize.setUrl((new File(timestamp, sessionLabel)).getAbsolutePath());
            initialize.setSource(getParameters().get(URIManager.SOURCE));
            initialize.setPreventAnon(Boolean.valueOf((String) getParameters().get(URIManager.PREVENT_ANON)));
            initialize.setPreventAutoCommit(Boolean.valueOf((String) getParameters().get(URIManager.PREVENT_AUTO_COMMIT)));

            getOrCreate = PrearcDatabase.eitherGetOrCreateSession(initialize, timestamp, shouldAutoArchive(project, dicom));

            if (getOrCreate.isLeft()) {
                session = getOrCreate.getLeft();
            } else {
                session = getOrCreate.getRight();
            }
        } catch (Exception e) {
            throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);
        }

        try {
            // else if the last mod time is more then 15 seconds ago, update it.
            // this code builds and executes the sql directly, because the APIs for doing so generate multiple SELECT statements (to confirm the row is there)
            // we've confirmed the row is there in line 338, so that shouldn't be necessary here.
            // this code executes for every file received, so any unnecessary sql should be eliminated.
            if (Calendar.getInstance().getTime().after(DateUtils.addSeconds(session.getLastBuiltDate(), 15))) {
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.updateSessionLastModSQL(session.getName(), session.getTimestamp(), session.getProject()), null, null);
            }
        } catch (Exception e) {
            log.error("An error occurred trying to update the session update timestamp.", e);
        }

        // Build the scan label
        final String seriesNum = dicom.getString(Tag.SeriesNumber);
        final String seriesUID = dicom.getString(Tag.SeriesInstanceUID);
        final String scan;
        if (Files.isValidFilename(seriesNum)) {
            scan = seriesNum;
        } else if (!Strings.isNullOrEmpty(seriesUID)) {
            scan = Labels.toLabelChars(seriesUID);
        } else {
            scan = null;
        }

        final String source = getString(getParameters(), SENDER_ID_PARAM, getUser().getLogin());

        final DicomObject fmi;
        if (dicom.contains(Tag.TransferSyntaxUID)) {
            fmi = dicom.fileMetaInfo();
        } else {
            final String sopClassUID       = dicom.getString(Tag.SOPClassUID);
            final String sopInstanceUID    = dicom.getString(Tag.SOPInstanceUID);
            final String transferSyntaxUID = getTransferSyntax() == null ? dicom.getString(Tag.TransferSyntaxUID, DEFAULT_TRANSFER_SYNTAX) : getTransferSyntax().uid();
            fmi = new BasicDicomObject();
            fmi.initFileMetaInformation(sopClassUID, sopInstanceUID, transferSyntaxUID);
            if (getParameters().containsKey(SENDER_AE_TITLE_PARAM)) {
                fmi.putString(Tag.SourceApplicationEntityTitle, VR.AE, (String) getParameters().get(SENDER_AE_TITLE_PARAM));
            }
        }

        MizerArchiveProcessor processor = new MizerArchiveProcessor();
        processor.process(fmi, fmi, session, getMizer());



//        try {
//            // check to see of this session came in through an application that may have performed anonymization
//            // prior to transfer, e.g. the XNAT Upload Assistant.
//            if (!session.getPreventAnon() && DefaultAnonUtils.getService().isSiteWideScriptEnabled()) {
//                Configuration c = DefaultAnonUtils.getCachedSitewideAnon();
//                if (c != null && c.getStatus().equals(Configuration.ENABLED_STRING)) {
//                    //noinspection deprecation
//                    Long scriptId = c.getId();
//
//                    if (scriptId == null) {
//                        throw new IllegalArgumentException("\"record\" is true, but \"scriptId\" is null.");
//                    } else {
//                        return Anonymize.addRecord(dicomObject, scriptId);
//                    }
//
//
//                    getMizer().anonymize(fmi, session.getProject(), session.getSubject(), session.getFolderName(), c.getContents());
//                } else {
//                    log.debug("Anonymization is not enabled, allowing session {} {} {} to proceed without anonymization.", session.getProject(), session.getSubject(), session.getName());
//                }
//            } else if (session.getPreventAnon()) {
//                log.debug("The session {} {} {} has already been anonymized by the uploader, proceeding without further anonymization.", session.getProject(), session.getSubject(), session.getName());
//            }
//        } catch (Throwable e) {
//            log.debug("Dicom anonymization failed: " + outputFile, e);
//            try {
//                // if we created a row in the database table for this session
//                // delete it.
//                if (getOrCreate.isRight()) {
//                    PrearcDatabase.deleteSession(session.getFolderName(), session.getTimestamp(), session.getProject());
//                } else {
//                    outputFile.delete();
//                }
//            } catch (Throwable t) {
//                log.debug("Unable to delete relevant file :" + outputFile, e);
//                throw new ServerException(Status.SERVER_ERROR_INTERNAL, t);
//            }
//            throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);
//        }

        final File sessionFolder = new File(new File(root, session.getTimestamp()), session.getFolderName());
        final File outputFile    = getSafeFile(sessionFolder, scan, name, dicom, Boolean.valueOf((String) getParameters().get(RENAME_PARAM)));
        outputFile.getParentFile().mkdirs();

        PrearcUtils.PrearcFileLock lock = null;
        try {
            lock = PrearcUtils.lockFile(session.getSessionDataTriple(), outputFile.getName());
            write(fmi, dicom, inputStream, outputFile, source, session);
        } catch (IOException e) {
            throw new ServerException(Status.SERVER_ERROR_INSUFFICIENT_STORAGE, e);
        } catch (PrearcUtils.SessionFileLockException e) {
            throw new ClientException("Concurrent file sends of the same data is not supported.");
        } finally {
            //release the file lock
            if(lock!=null){
                lock.release();
            }
        }



        log.trace("Stored object {}/{}/{} as {} for {}", project, studyInstanceUID, dicom.getString(Tag.SOPInstanceUID), session.getUrl(), source);
        return Collections.singletonList(session.getExternalUrl());
    }

    protected TransferSyntax getTransferSyntax() {
        return _transferSyntax;
    }

    private final TransferSyntax _transferSyntax;
}
package org.nrg.xnat.archive.processors;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che2.data.DicomObject;
import org.nrg.action.ServerException;
import org.nrg.config.entities.Configuration;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.restlet.data.Status;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MizerArchiveProcessor extends AbstractArchiveProcessor {

    @Override
    public void process(final DicomObject metadata, final SessionData sessionData) {
        process(metadata, metadata, sessionData);
    }


    // Taken from GradulDicomImportOperation lines 277-302
    @Override
    public void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData) {
        try {
            // check to see of this session came in through an application that may have performed anonymization
            // prior to transfer, e.g. the XNAT Upload Assistant.
            if (!session.getPreventAnon() && DefaultAnonUtils.getService().isSiteWideScriptEnabled()) {
                Configuration c = DefaultAnonUtils.getCachedSitewideAnon();
                if (c != null && c.getStatus().equals(Configuration.ENABLED_STRING)) {
                    //noinspection deprecation
                    getMizer().anonymize(outputFile, session.getProject(), session.getSubject(), session.getFolderName(), true, c.getId(), c.getContents());
                } else {
                    log.debug("Anonymization is not enabled, allowing session {} {} {} to proceed without anonymization.", session.getProject(), session.getSubject(), session.getName());
                }
            } else if (session.getPreventAnon()) {
                log.debug("The session {} {} {} has already been anonymized by the uploader, proceeding without further anonymization.", session.getProject(), session.getSubject(), session.getName());
            }
        } catch (Throwable e) {
            log.debug("Dicom anonymization failed: " + outputFile, e);
            try {
                // if we created a row in the database table for this session
                // delete it.
                if (getOrCreate.isRight()) {
                    PrearcDatabase.deleteSession(session.getFolderName(), session.getTimestamp(), session.getProject());
                } else {
                    outputFile.delete();
                }
            } catch (Throwable t) {
                log.debug("Unable to delete relevant file :" + outputFile, e);
                throw new ServerException(Status.SERVER_ERROR_INTERNAL, t);
            }
            throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);

    }
}

package org.nrg.xnat.archive.processors;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che2.data.DicomObject;
import org.nrg.action.ServerException;
import org.nrg.config.entities.Configuration;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.nrg.dicom.mizer.service.MizerService;

@Component
@Slf4j
public class MizerArchiveProcessor extends AbstractArchiveProcessor {

    @Override
    public void process(final DicomObject metadata, final SessionData sessionData) throws ServerException {
        process(metadata, metadata, sessionData);
    }

    @Override
    public void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData) throws ServerException {
        process(metadata, metadata, sessionData, null);
    }

    @Override
    public void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData, final MizerService mizer) throws ServerException{
        try {
            // check to see of this session came in through an application that may have performed anonymization
            // prior to transfer, e.g. the XNAT Upload Assistant.
            if (!sessionData.getPreventAnon() && DefaultAnonUtils.getService().isSiteWideScriptEnabled()) {
                Configuration c = DefaultAnonUtils.getCachedSitewideAnon();
                if (c != null && c.getStatus().equals(Configuration.ENABLED_STRING)) {
                    //noinspection deprecation
                    Long scriptId = c.getId();

                    mizer.anonymize(metadata, sessionData.getProject(), sessionData.getSubject(), sessionData.getFolderName(), c.getContents());
                } else {
                    log.debug("Anonymization is not enabled, allowing session {} {} {} to proceed without anonymization.", sessionData.getProject(), sessionData.getSubject(), sessionData.getName());
                }
            } else if (sessionData.getPreventAnon()) {
                log.debug("The session {} {} {} has already been anonymized by the uploader, proceeding without further anonymization.", sessionData.getProject(), sessionData.getSubject(), sessionData.getName());
            }
        } catch (Throwable e) {
            log.debug("Dicom anonymization failed: " + metadata, e);
            throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }
}
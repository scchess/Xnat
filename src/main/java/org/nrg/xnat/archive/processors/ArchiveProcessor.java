package org.nrg.xnat.archive.processors;

import org.dcm4che2.data.DicomObject;
import org.nrg.action.ServerException;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.dicom.mizer.service.MizerService;

public interface ArchiveProcessor {
    void process(final DicomObject metadata, final SessionData sessionData) throws ServerException;
    void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData) throws ServerException;
    void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData, final MizerService mizer) throws ServerException;
}

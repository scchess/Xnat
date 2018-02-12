package org.nrg.xnat.archive.processors;

import org.dcm4che2.data.DicomObject;
import org.nrg.xnat.helpers.prearchive.SessionData;

public interface ArchiveProcessor {
    void process(final DicomObject metadata, final SessionData sessionData);
    void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData);
}

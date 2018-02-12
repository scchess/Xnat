package org.nrg.xnat.archive.processors;

import org.dcm4che2.data.DicomObject;
import org.nrg.xnat.helpers.prearchive.SessionData;

public abstract class AbstractArchiveProcessor implements ArchiveProcessor {
    @Override
    public abstract void process(final DicomObject metadata, final SessionData sessionData);

    @Override
    public abstract void process(final DicomObject metadata, final DicomObject imageData, final SessionData sessionData);
}

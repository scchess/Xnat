/*
 * web: org.nrg.xnat.archive.GradualDicomImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.archive;

import org.apache.commons.lang3.ObjectUtils;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.archive.operations.DicomImportOperation;
import org.nrg.xnat.archive.operations.GradualDicomImportOperation;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.restlet.actions.importer.ImporterHandler;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ThrowFromFinallyBlock")
@Service
@ImporterHandler(handler = ImporterHandlerA.GRADUAL_DICOM_IMPORTER)
public class GradualDicomImporter extends ImporterHandlerA {
    public static final String SENDER_AE_TITLE_PARAM = "Sender-AE-Title";
    public static final String SENDER_ID_PARAM       = "Sender-ID";
    public static final String TSUID_PARAM           = "Transfer-Syntax-UID";

//    @Autowired
//    public GradualDicomImporter(final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer) {
//        super(identifier, namer, processors, filterService, mizer);
//    }

    @Autowired
    public GradualDicomImporter(final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer, final List<ArchiveProcessor> processors) {
        super(identifier, namer, processors, filterService, mizer);
    }

    @Override
    public DicomImportOperation getOperation(final Object listenerControl, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> params, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer) {
        return new GradualDicomImportOperation(listenerControl, user, writer, params, getProcessors(), getFilterService(), ObjectUtils.defaultIfNull(identifier, getIdentifier()), getMizer(), ObjectUtils.defaultIfNull(namer, getNamer()));
    }
}

/*
 * web: org.nrg.xnat.restlet.actions.XarImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.actions;

import org.apache.commons.lang3.ObjectUtils;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.apache.log4j.Logger;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.*;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.zip.TarUtils;
import org.nrg.xft.utils.zip.ZipI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.archive.operations.DicomImportOperation;
import org.nrg.xnat.archive.operations.XarImportOperation;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.restlet.actions.importer.ImporterHandler;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.ZipOutputStream;

@ImporterHandler(handler = ImporterHandlerA.XAR_IMPORTER)
public class XarImporter extends ImporterHandlerA {

	private static final Logger logger = Logger.getLogger(XarImporter.class);

    @Autowired
    public XarImporter(final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer) {
        super(identifier, namer, processors, filterService, mizer);
    }

    @Override
    public DicomImportOperation getOperation(Object listenerControl, UserI user, FileWriterWrapperI writer, Map<String, Object> params, DicomObjectIdentifier<XnatProjectdata> identifier, DicomFileNamer namer) {
        return new XarImportOperation(listenerControl, user, writer, params, getProcessors(), getFilterService(), ObjectUtils.defaultIfNull(identifier, getIdentifier()), getMizer(), ObjectUtils.defaultIfNull(namer, getNamer()));
    }
}

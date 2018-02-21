/*
 * web: org.nrg.xnat.restlet.actions.SessionImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ObjectUtils;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.xnat.DicomObjectIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.status.StatusProducer;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xnat.archive.operations.SessionImportOperation;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.helpers.prearchive.*;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.status.ListenerUtils;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.archive.FinishImageUpload;
import org.nrg.xnat.helpers.PrearcImporterHelper;
import org.nrg.xnat.helpers.merge.SiteWideAnonymizer;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.restlet.actions.PrearcImporterA.PrearcSession;
import org.nrg.xnat.restlet.actions.importer.ImporterHandler;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.restlet.util.RequestUtil;
import org.nrg.xnat.turbine.utils.XNATSessionPopulater;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

@ImporterHandler(handler = ImporterHandlerA.SESSION_IMPORTER)
public class SessionImporter extends ImporterHandlerA {

	private static final Logger logger = LoggerFactory.getLogger(SessionImporter.class);

	@Autowired
	public SessionImporter(final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer) {
		super(identifier, namer, processors, filterService, mizer);
	}

	@Override
	public SessionImportOperation getOperation(Object listenerControl, UserI user, FileWriterWrapperI writer, Map<String, Object> params, DicomObjectIdentifier<XnatProjectdata> identifier, DicomFileNamer namer) {
		return new SessionImportOperation(listenerControl, user, writer, params, getProcessors(), getFilterService(), ObjectUtils.defaultIfNull(identifier, getIdentifier()), getMizer(), ObjectUtils.defaultIfNull(namer, getNamer()));
	}
}

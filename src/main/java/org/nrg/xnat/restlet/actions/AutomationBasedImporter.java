/*
 * web: org.nrg.xnat.restlet.actions.AutomationBasedImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.actions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.automation.entities.ScriptOutput;
import org.nrg.automation.entities.ScriptOutput.Status;
import org.nrg.automation.entities.ScriptTrigger;
import org.nrg.automation.event.AutomationEventImplementerI;
import org.nrg.automation.event.entities.AutomationCompletionEvent;
import org.nrg.automation.event.entities.AutomationEventIdsIds;
import org.nrg.automation.services.AutomationEventIdsIdsService;
import org.nrg.automation.services.AutomationEventIdsService;
import org.nrg.automation.services.ScriptTriggerService;
import org.nrg.framework.constants.Scope;
import org.nrg.framework.event.Filterable;
import org.nrg.framework.services.NrgEventService;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.CATEGORY;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.entities.WorkflowStatusEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.EventRequirementAbsent;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.zip.TarUtils;
import org.nrg.xft.utils.zip.ZipI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.archive.operations.AutomationBasedImportOperation;
import org.nrg.xnat.archive.operations.SessionImportOperation;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.event.listeners.AutomationCompletionEventListener;
import org.nrg.xnat.processor.services.ArchiveProcessorInstanceService;
import org.nrg.xnat.restlet.actions.importer.ImporterHandler;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.files.utils.RestFileUtils;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.apache.commons.lang3.ObjectUtils;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.xnat.DicomObjectIdentifier;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.ZipOutputStream;

@ImporterHandler(handler = "automation", allowCallsWithoutFiles = true, callPartialUriWrap = false)
public class AutomationBasedImporter extends ImporterHandlerA {

	public AutomationBasedImporter(final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer, final ArchiveProcessorInstanceService processorInstanceService) {
		super(identifier, namer, processors, filterService, mizer, processorInstanceService);
	}

	@Override
	public AutomationBasedImportOperation getOperation(Object listenerControl, UserI user, FileWriterWrapperI writer, Map<String, Object> params, DicomObjectIdentifier<XnatProjectdata> identifier, DicomFileNamer namer) {
		return new AutomationBasedImportOperation(listenerControl, user, writer, params, getProcessors(), getFilterService(), ObjectUtils.defaultIfNull(identifier, getIdentifier()), getMizer(), ObjectUtils.defaultIfNull(namer, getNamer()), getProcessorService());
	}

}

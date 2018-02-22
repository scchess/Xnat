/*
 * web: org.nrg.xnat.restlet.actions.importer.ImporterHandlerA
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.actions.importer;

import lombok.extern.slf4j.Slf4j;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dcm.xnat.SOPHashDicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.turbine.utils.PropertiesHelper;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.archive.operations.DicomImportOperation;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
@Slf4j
public abstract class ImporterHandlerA {
    protected ImporterHandlerA(final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final List<ArchiveProcessor> processors, final DicomFilterService filterService, final MizerService mizer) {
        _identifier = identifier;
        _namer = namer;
        _processors = processors;
        _filterService = filterService;
        _mizer = mizer;
    }

    public abstract DicomImportOperation getOperation(final Object listenerControl, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> params, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer);

    public DicomImportOperation getOperation(final Object listenerControl, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> params) {
        return getOperation(listenerControl, user, writer, params, getIdentifier(), getNamer());
    }

    @Async
    public Future<List<String>> doImport(final Object listenerControl, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> params) throws Exception {
        return doImport(listenerControl, user, writer, params, null, null);
    }

    @Async
    public Future<List<String>> doImport(final Object listenerControl, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> params, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer) throws Exception {
        return doImport(getOperation(listenerControl, user, writer, params, identifier, namer));
    }

    @Async
    public Future<List<String>> doImport(final DicomImportOperation operation) throws Exception {
        return new AsyncResult<>(operation.call());
    }

    public DicomObjectIdentifier<XnatProjectdata> getIdentifier() {
        return _identifier;
    }

    public DicomFileNamer getNamer() {
        if (_namer == null) {
            return DEFAULT_NAMER;
        }
        return _namer;
    }

    public List<ArchiveProcessor> getProcessors() {
        return _processors;
    }

    public DicomFilterService getFilterService() {
        return _filterService;
    }

    public MizerService getMizer() {
        return _mizer;
    }

    public static final String IMPORT_HANDLER_ATTR = "import-handler";

    public static final String SESSION_IMPORTER       = "SI";
    public static final String XAR_IMPORTER           = "XAR";
    public static final String GRADUAL_DICOM_IMPORTER = "gradual-DICOM";
    public static final String DICOM_ZIP_IMPORTER     = "DICOM-zip";
    public static final String BLANK_PREARCHIVE_ENTRY = "blank";

    static       String                                         DEFAULT_HANDLER = SESSION_IMPORTER;
    final static Map<String, Class<? extends ImporterHandlerA>> IMPORTERS       = new HashMap<>();

    private static final String   PROP_OBJECT_IDENTIFIER = "org.nrg.import.handler.impl";
    private static final String   IMPORTER_PROPERTIES    = "importer.properties";
    private static final String   CLASS_NAME             = "className";
    private static final String[] PROP_OBJECT_FIELDS     = new String[]{CLASS_NAME};

//    static {
//        //First, find importers by property file (if it exists)
//        //EXAMPLE PROPERTIES FILE
//        //org.nrg.import.handler=NIFTI
//        //org.nrg.import.handler.impl.NIFTI.className=org.nrg.import.handler.CustomNiftiImporter:w
//        try {
//            IMPORTERS.putAll((new PropertiesHelper<ImporterHandlerA>()).buildClassesFromProps(IMPORTER_PROPERTIES, PROP_OBJECT_IDENTIFIER, PROP_OBJECT_FIELDS, CLASS_NAME));
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//        //Second, find importers by annotation
//        final ImporterHandlerPackages packages = XDAT.getContextService().getBean("importerHandlerPackages", ImporterHandlerPackages.class);
//        for (final String pkg : packages) {
//            try {
//                final List<Class<?>> classesForPackage = Reflection.getClassesForPackage(pkg);
//                for (final Class<?> clazz : classesForPackage) {
//                    if (ImporterHandlerA.class.isAssignableFrom(clazz)) {
//                        if (!clazz.isAnnotationPresent(ImporterHandler.class)) {
//                            continue;
//                        }
//                        ImporterHandler anno = clazz.getAnnotation(ImporterHandler.class);
//                        if (anno != null && !IMPORTERS.containsKey(anno.handler())) {
//                            if (log.isDebugEnabled()) {
//                                log.debug("Found ImporterHandler: " + clazz.getName());
//                            }
//                            IMPORTERS.put(anno.handler(), (Class<? extends ImporterHandlerA>) clazz);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    /**
     * This method was added to allow other developers to manually add importers to the list, without adding a
     * configuration file.  However, this would some how need to be done before the import is executed (maybe as a
     * servlet?).
     *
     * @return A map of the currently configured importers.
     */
    @SuppressWarnings("unused")
    public static Map<String, Class<? extends ImporterHandlerA>> getImporters() {
        return IMPORTERS;
    }

    private static final DicomFileNamer DEFAULT_NAMER = new SOPHashDicomFileNamer();

    private final DicomObjectIdentifier<XnatProjectdata>  _identifier;
    private final DicomFileNamer         _namer;
    private final List<ArchiveProcessor> _processors;
    private final DicomFilterService     _filterService;
    private final MizerService           _mizer;
}

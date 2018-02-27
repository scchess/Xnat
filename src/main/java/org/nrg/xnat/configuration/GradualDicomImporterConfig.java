/*
 * web: org.nrg.xnat.configuration.DicomImportConfig
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dcm.id.ClassicDicomObjectIdentifier;
import org.nrg.dcm.id.TemplatizedDicomFileNamer;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.xnat.archive.GradualDicomImporter;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.processor.services.ArchiveProcessorInstanceService;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerPackages;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.nrg.xnat.utils.XnatUserProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;


import java.io.IOException;
import java.util.*;

import org.nrg.dicom.mizer.service.MizerService;

@Configuration
@ComponentScan({"org.nrg.dcm.scp", "org.nrg.dcm.edit.mizer", "org.nrg.dicom.dicomedit.mizer", "org.nrg.dicom.mizer.service.impl"})
public class GradualDicomImporterConfig {
    @Bean
    @Primary
    public GradualDicomImporter gradualDicomImporter(final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final DicomFileNamer namer, final MizerService mizer, final List<ArchiveProcessor> processors, final ArchiveProcessorInstanceService processorInstanceService) {
         return new GradualDicomImporter(filterService, identifier, namer, mizer, processors, processorInstanceService);
    }
}

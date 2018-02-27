package org.nrg.xnat.archive.operations;

import com.google.common.collect.Sets;
import org.dcm4che2.io.DicomInputStream;
import org.nrg.action.ClientException;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.helpers.ZipEntryFileWriterWrapper;
import org.nrg.xnat.processor.services.ArchiveProcessorInstanceService;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipDicomImportOperation extends GradualDicomImportOperation {
    public ZipDicomImportOperation(final Object control, final UserI user, final FileWriterWrapperI fileWriter, final Map<String, Object> parameters, final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final MizerService mizer, final DicomFileNamer namer, final ArchiveProcessorInstanceService processorInstanceService) {
        super(control, user, fileWriter, parameters, processors, filterService, identifier, mizer, namer, processorInstanceService);
    }

    @Override
    public List<String> call() throws Exception {
        final Set<String> uris = Sets.newLinkedHashSet();
        try (final ZipInputStream zipInputStream = new ZipInputStream(getFileWriter().getInputStream())) {
            ZipEntry          zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    final ZipEntryFileWriterWrapper wrapper = new ZipEntryFileWriterWrapper(zipEntry, zipInputStream);
                    try (final InputStream inputStream = wrapper.getInputStream(); final DicomInputStream dicomInputStream = new DicomInputStream(inputStream)) {
                        uris.addAll(processDicomInputStream(inputStream, dicomInputStream));
                    }
                }
            }
        } catch (IOException e) {
            throw new ClientException("unable to read data from zip file", e);
        }
        return new ArrayList<>(uris);
    }
}

package org.nrg.xapi.model.dicomweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to keep the underlying DICOM library from leaking.
 */
public interface DicomObjectI {

//    String getStudyInstanceUID();
//
//    String getSeriesInstanceUID();
//
//    String getSOPInstanceUID();

    void write(OutputStream os) throws IOException;

    InputStream getInputStream() throws IOException;

    File getFile() ;

    String getTransferSyntaxUID();
}

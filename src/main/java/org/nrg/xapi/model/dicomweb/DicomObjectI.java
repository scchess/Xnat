package org.nrg.xapi.model.dicomweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by davidmaffitt on 7/9/17.
 */
public interface DicomObjectI {

    String getStudyInstanceUID();

    String getSeriesInstanceUID();

    String getSOPInstanceUID();

    void write(OutputStream os) throws IOException;

    String getMessage();

    InputStream getInputStream() throws IOException;

    File getFile() ;
}

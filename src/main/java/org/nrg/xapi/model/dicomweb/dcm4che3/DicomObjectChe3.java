package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.io.DicomInputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;

import java.io.*;

public class DicomObjectChe3 implements DicomObjectI{

    private final File file;

    public DicomObjectChe3(File file) {
        this.file = file;
    }

    /**
     * Blast the file, as is, down stream.
     *
     * @param os
     * @throws IOException
     */
    @Override
    public void write(OutputStream os) throws IOException {
        byte[] buf = new byte[16384];
        try ( InputStream is = new FileInputStream(file)) {
            int bytes;
            while( (bytes = is.read(buf)) != -1) {
                os.write(buf, 0, bytes);
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new DicomInputStream( file);
    }

    @Override
    public File getFile() {
        return null;
    }
}

package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.DatasetWithFMI;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputHandler;
import org.dcm4che3.io.DicomInputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;

import java.io.*;

public class DicomObjectChe3 implements DicomObjectI{

    private final File file;
    private Attributes attributes = null;

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
        return file;
    }

    /**
     * Return the transfer syntax uid or null if it is missing or IO error.
     *
     * @return
     */
    @Override
    public String getTransferSyntaxUID() {
        try {
            return getAttributeValue(Tag.TransferSyntaxUID);
        }
        catch (IOException e) {
            return null;
        }
    }

    public String getAttributeValue( int tag) throws IOException {
        if( attributes == null) {
            readHeader();
        }
        return attributes.getString( tag);
    }

    private void readHeader() throws IOException {
        DicomInputStream dis = new DicomInputStream( file);
//        attributes = dis.readDataset(-1, Tag.PixelData);
        DatasetWithFMI data  = dis.readDatasetWithFMI(-1, Tag.PixelData);

        attributes = data.getFileMetaInformation();
        attributes.addAll( data.getDataset());
    }
}

package org.nrg.xapi.model.dicomweb.dcm4che2;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by davidmaffitt on 7/9/17.
 */
public class DicomObjectChe2 implements DicomObjectI {

    private DicomObject _dicomObject;
    private File _file;

    public DicomObjectChe2( File f) throws IOException {
        _file = f;
        DicomInputStream dis = new DicomInputStream( f);
        _dicomObject = dis.readDicomObject();
    }

    public DicomObjectChe2( InputStream is) throws IOException {
        DicomInputStream dis = new DicomInputStream( is);
        _dicomObject = dis.readDicomObject();
    }

    public String getStudyInstanceUID() {
        return null;
    }

    public String getSeriesInstanceUID() {
        return null;
    }

    public String getSOPInstanceUID() {
        return null;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        DicomOutputStream dos = new DicomOutputStream(os);
        dos.writeDicomFile( _dicomObject);
    }

    public String getMessage() {
        return "Hey oh!";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        DicomInputStream dis = new DicomInputStream(_file);
        return dis;
    }

    @Override
    public File getFile() {
        return _file;
    }
}

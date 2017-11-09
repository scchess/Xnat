package org.nrg.xapi.model.dicomweb;

import org.nrg.xapi.model.dicomweb.dcm4che2.DicomObjectChe2;
import org.nrg.xapi.model.dicomweb.dcm4che3.DicomObjectChe3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by davidmaffitt on 7/9/17.
 */
public class DicomObjectFactory {

    public static DicomObjectI create(File f) throws IOException {
        return new DicomObjectChe3(f);
    }

//    public static DicomObjectI create( InputStream is) throws IOException {
//        return new DicomObjectChe2( is);
//    }
//
//    public static DicomObjectI create() {
//        return null;
//    }
}

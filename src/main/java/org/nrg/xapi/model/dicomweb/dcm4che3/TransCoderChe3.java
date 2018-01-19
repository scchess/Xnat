package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.text.MessageFormat;

@Component
public class TransCoderChe3 implements TransCoder {

    Dcm2Dcm dcm2Dcm;

    public TransCoderChe3() {
        this.dcm2Dcm = new Dcm2Dcm();
    }

    @Override
    public void transcode(DicomObjectI inDcm, String dstTsuid, OutputStream os) throws TransCoderException {
        String srcTsuid = inDcm.getTransferSyntaxUID();
        try {
            if ( srcTsuid != null && srcTsuid.equals(dstTsuid)) {
                inDcm.write(os);
            }
            else {
                dcm2Dcm.setTransferSyntax( dstTsuid);

                dcm2Dcm.transcode( new DicomInputStream( inDcm.getFile()), new DicomOutputStream(os, dstTsuid));
            }
        }
        catch( Exception e) {
            String msg = MessageFormat.format("Error transcoding DICOM object from transferSyntax {0} to {1}", srcTsuid, dstTsuid);
            throw new TransCoderException(msg, e);
        }
    }

    @Override
    public boolean isSupportedTransferSyntax(String tranferSyntaxUID) {
        return dcm2Dcm.isSupportedTransferSyntax( tranferSyntaxUID);
    }

}

package org.nrg.xapi.model.dicomweb;

import java.io.OutputStream;

public interface TransCoder {

    void transcode( DicomObjectI dcmObj, String transferSyntaxUID, OutputStream os) throws TransCoderException;

    boolean isSupportedTransferSyntax( String tranferSyntaxUID);
}

package org.nrg.xapi.model.dicomweb;

public class TransCoderException extends RuntimeException {

    public TransCoderException( String msg, Exception e) {
        super( msg, e);
    }
}

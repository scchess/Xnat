package org.nrg.xapi.model.dicomweb;

public class UnsupportedTransferSyntaxException extends RuntimeException {

    private String tsuid;

    public UnsupportedTransferSyntaxException( String tsuid) {
        super( "Unsupported transfer syntax: " + tsuid);
        this.tsuid = tsuid;
    }

    public UnsupportedTransferSyntaxException( String tsuid, RuntimeException e) {
        super( "Unsupported transfer syntax: " + tsuid, e);
        this.tsuid = tsuid;
    }
}

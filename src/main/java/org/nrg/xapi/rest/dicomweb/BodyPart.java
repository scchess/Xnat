package org.nrg.xapi.rest.dicomweb;

import org.springframework.http.MediaType;

/**
 * Created by davidmaffitt on 8/22/17.
 */
public class BodyPart<T> {
    private T body;
    private MediaType mediaType;

    public BodyPart( T body, MediaType mediaType) {
        this.body = body;
        this.mediaType = mediaType;
    }

    public T getBody() {
        return body;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}

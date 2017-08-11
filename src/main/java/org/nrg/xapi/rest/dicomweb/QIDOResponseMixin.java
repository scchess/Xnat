package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dcm4che3.data.Attributes;
import org.nrg.framework.annotations.XnatMixIn;
import org.nrg.xapi.model.dicomweb.QIDOResponse;

@JsonSerialize(using=JsonDicomObjectSerializer.class)
public class QIDOResponseMixin {

}

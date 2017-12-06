package org.nrg.xapi.model.dicomweb;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.io.SAXWriter;
import org.nrg.framework.annotations.XnatMixIn;
import org.nrg.xapi.rest.dicomweb.QIDOResponseMixin;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class QIDOResponseStudy extends QIDOResponse {

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"


    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, ElementDictionary.vrOf(0x00080005,null), value ); }

    public String getStudyDate() { return getString( 0x00080020); }
    public void setStudyDate(String value) { setString( 0x00080020, ElementDictionary.vrOf(0x00080020, null), value ); }
    public void setStudyDate(Object obj) { setString( 0x00080020, ElementDictionary.vrOf(0x00080020, null), (obj == null)? null:dateFormat.format( obj) ); }

    public String getStudyTime() { return getString(0x00080030); }
    public void setStudyTime(String value) { setString( 0x00080030, ElementDictionary.vrOf(0x00080030, null), value ); }
    public void setStudyTime(Object obj) { setString( 0x00080030, ElementDictionary.vrOf(0x00080030, null), (obj == null)? null:timeFormat.format( obj) ); }

    public String getAccessionNumber() { return getString(0x00080050); }
    public void setAccessionNumber(String value) { setString( 0x00080050, ElementDictionary.vrOf(0x00080050, null), value ); }

    public String getInstanceAvailability() { return getString(0x00080056); }
    public void setInstanceAvailability(String value) { setString( 0x00080056, ElementDictionary.vrOf(0x00080056, null), value ); }

    public String getModalitiesInStudy() { return getString(0x00080061); }
    public void setModalitiesInStudy(String value) { setString( 0x00080061, ElementDictionary.vrOf(0x00080061, null), value ); }

    public String getReferringPhysiciansName() { return getString(0x00080090); }
    public void setReferringPhysiciansName(String value) { setString( 0x00080090, ElementDictionary.vrOf(0x00080090, null), value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

    public String getRetrieveURL() { return getString(0x00081190); }
    public void setRetrieveURL(String value) { setString( 0x00081190, ElementDictionary.vrOf(0x00081190, null), value ); }

    public String getPatientsName() { return getString(0x00100010); }
    public void setPatientsName(String value) { setString( 0x00100010, ElementDictionary.vrOf(0x00100010, null), value ); }

    public String getPatientID() { return getString(0x00100020); }
    public void setPatientID(String value) { setString( 0x00100020, ElementDictionary.vrOf(0x00100020, null), value ); }

    public String getPatientsBirthDate() { return getString(0x00100030); }
    public void setPatientsBirthDate(String value) { setString( 0x00100030, ElementDictionary.vrOf(0x00100030, null), value ); }

    public String getPatientsSex() { return getString(0x00100040); }
    public void setPatientsSex(String value) { setString( 0x00100040, ElementDictionary.vrOf(0x00100040, null), value ); }

    public String getStudyInstanceUID() { return getString(0x0020000D); }
    public void setStudyInstanceUID(String value) { setString( 0x0020000D, ElementDictionary.vrOf(0x0020000D, null), value ); }

    public String getStudyID() { return getString(0x00200010); }
    public void setStudyID(String value) { setString( 0x00200010, ElementDictionary.vrOf(0x00200010, null), value ); }

//    public String getNumberOfStudyRelatedSeries() { return getString(0x00201206); }
    public int getNumberOfStudyRelatedSeries() { return getInt(0x00201206, 0); }
    public void setNumberOfStudyRelatedSeries(String value) { setString( 0x00201206, ElementDictionary.vrOf(0x00201206, null), value ); }
    public void setNumberOfStudyRelatedSeries(int intValue) { setInt( 0x00201206, ElementDictionary.vrOf(0x00201206, null), intValue ); }

//    public String getNumberOfStudyRelatedInstances() { return getString(0x00201208); }
    public int getNumberOfStudyRelatedInstances() { return getInt(0x00201208, 0); }
    public void setNumberOfStudyRelatedInstances(String value) { setString( 0x00201208, ElementDictionary.vrOf(0x00201208, null), value ); }
    public void setNumberOfStudyRelatedInstances(int intValue) { setInt( 0x00201208, ElementDictionary.vrOf(0x00201208, null), intValue ); }

}

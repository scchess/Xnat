package org.nrg.xapi.rest.dicomweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Translate parameter names between the DICOM and XNAT domains.
 *
 * A DICOM query param can be repeated.
 *
 */
public abstract class QueryParameters {

    private Map<String, List<String>> normalizedMap;
    private static final Logger _log = LoggerFactory.getLogger(QueryParameters.class);

    public QueryParameters() {
        normalizedMap = new HashMap<>();
    }

    public QueryParameters(Map<String, String> dicomRequestParams) {
        this();
        for( String paramName: dicomRequestParams.keySet()) {
            addDicomParameter( paramName, dicomRequestParams.get(paramName));
        }
    }

    /**
     * Add the value of the DICOM-named parameter to the map.  It will be stored under its normalized name.
     *
     * @param dicomParamName
     * @param value
     */
    public abstract void  addDicomParameter(String dicomParamName, String value) ;

    public void addParam( String key, String value) {
        List<String> values;
        if( ! normalizedMap.containsKey( key)) {
            values = new ArrayList<>();
            normalizedMap.put( key, values);
        }
        else {
            values = normalizedMap.get( key);
        }
        values.add( value);
    }

    /**
     * Return the values mapped to the normalized key.
     *
     * @param key
     * @return the values mapped to the key, null if the key is not found.
     */
    public List<String> getParams( String key) {
        return normalizedMap.get( key);
    }

    /**
     * Return the values mapped to the normalized key as a single comma-delimited string.
     *
     * @param key
     * @return empty string if no values are mapped to the key.
     */
    public String getParamsString( String key) {
        StringBuilder sb = new StringBuilder();
        List<String> values = normalizedMap.get( key);
        for( int i = 0; i < values.size(); ) {
            sb.append( values.get(i));
            i++;
            if( i < values.size()) sb.append(",");
        }
        return sb.toString();
    }

    public Map<String, List<String>> getParams() {
        return normalizedMap;
    }

    public Set<String> keySet() {
        return normalizedMap.keySet();
    }

    public List<String> parseUIDs( String uids) {
        return Arrays.asList( uids.split("\\\\"));
    }

    public String asString(String paramName) {
        return paramName + "=" + getParamsString( paramName);
    }
}

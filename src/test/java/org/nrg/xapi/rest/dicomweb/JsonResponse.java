package org.nrg.xapi.rest.dicomweb;

import javax.json.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonResponse {

    public String getValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonString(0) != null) {
                    value = response.getJsonObject(tag).getJsonArray("Value").getJsonString(0).getString();
                }
            }
        }
        return value;
    }

    public String getIntegerValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonNumber(0) != null) {
                    value = response.getJsonObject(tag).getJsonArray("Value").getJsonNumber(0).toString();
                }
            }
        }
        return value;
    }

    public String getPNValueFromJson( JsonObject response, String tag) {
        String value = null;

        if( response.getJsonObject(tag) != null) {
            if( response.getJsonObject(tag).getJsonArray("Value") != null) {
                if( response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0) != null) {
                    if( response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0).getJsonString("Alphabetic") != null) {
                        value = response.getJsonObject(tag).getJsonArray("Value").getJsonObject(0).getJsonString("Alphabetic").getString();
                    }
                }
            }
        }
        return value;
    }


    public void assertEqualsCustom( String msg, String expected, String actual) {
        switch( expected) {
            case "ignore":
                return;
            case "null":
                assertNull(msg, actual);
                break;
            default:
                assertEquals( msg, expected, actual);
        }
    }
}

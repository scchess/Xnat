package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class SearchForSeries {

    private static String host = "http://xnat-latest";

    @Test
    public void knownStudy_ByModality() {
        try {

            List<String> expectedSeriesUIDs = Arrays.asList(
                    "1.3.6.1.4.1.14519.5.2.1.3344.2526.290843139448249569449245338151",
                    "1.3.6.1.4.1.14519.5.2.1.3344.2526.641668975271574884290627862002");
            String expectedModality = "MR";

            URL url = new URL(host + "/xapi/dicomweb/studies/1.3.6.1.4.1.14519.5.2.1.1188.4001.213420711084714071744561785405/series?Modality=MR");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/dicom+json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Authorization", "Basic dXNlcjI6dXNlcjI=");

            if (conn.getResponseCode() != 200) {
                fail("Failed : HTTP error code : " + conn.getResponseCode());
            }

            JsonReader rdr = Json.createReader(conn.getInputStream());

            JsonArray responses = rdr.readArray();
            assertEquals( expectedSeriesUIDs.size(), responses.size());

            for( JsonValue responseValue: responses) {
                JsonObject response = (JsonObject) responseValue;

                String actualModality = response.getJsonObject("00080060").getJsonArray("Value").getJsonString(0).getString();
                assertEquals( expectedModality, actualModality);

                String seriesUID = response.getJsonObject("0020000E").getJsonArray("Value").getJsonString(0).getString();
                assertTrue( expectedSeriesUIDs.contains( seriesUID));
            }

            conn.disconnect();

        } catch ( IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
}

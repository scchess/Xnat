package org.nrg.xapi.rest.dicomweb;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class InitializeXNAT {

    private String host;
    private String userAuthenticationHeaderValue;

    public InitializeXNAT( String host, String userAuthenticationHeaderValue) {
        this.host = host;
        this.userAuthenticationHeaderValue = userAuthenticationHeaderValue;
    }

    public void createProject( String id, String name) {

        sendPUTRequest( "/data/projects/" + id, "name=" + name);
    }

    public void createSubject( String projectId, String subjectLabel) {
        sendPUTRequest( "/data/projects/" + projectId + "/subjects/" + subjectLabel, null);
    }

    public void sendPUTRequest( String path, String params) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(host + path + "?" + ((params != null)? URLEncoder.encode(params, StandardCharsets.UTF_8.toString()): "") );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Authorization", userAuthenticationHeaderValue);

            if (conn.getResponseCode() < 300) {
                System.out.println("Success : HTTP status code : " + conn.getResponseCode() + " - " + conn.getResponseMessage());
                System.out.println("Response...");
                streamResponse( conn.getInputStream(), System.out);
            }
            else {
                System.out.println("Failed : HTTP error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage());
                System.out.println("Response...");
                streamResponse( conn.getErrorStream(), System.out);
//                System.out.println( conn.getContent());
//                streamResponse( conn.getErrorStream(), System.out);
//                System.out.println( conn.getResponseMessage());
            }
        }
        catch( IOException e) {
            e.printStackTrace();
        }
        finally {
            if( conn != null) {conn.disconnect();}
        }
    }

    private void streamResponse(InputStream is, OutputStream os) throws IOException {
        PrintStream ps = new PrintStream(os);
        BufferedReader br = new BufferedReader(new InputStreamReader((is)));

        String output;
        while ((output = br.readLine()) != null) {
            ps.println(output);
        }


    }

    public static void main(String[] args) {
        InitializeXNAT xnat = new InitializeXNAT( "http://xnat-latest", "Basic YWRtaW46YWRtaW4=");

//        xnat.createProject("projectPut2", "Name of projectPut2");
        xnat.createSubject("projectPut2", "subjectPut22");
    }

}

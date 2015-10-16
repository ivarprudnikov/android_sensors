package com.ivarprudnikov.sensors.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpUtil {

    public static Map post(String urlString, Map data, Map headers){

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e){
            return null;
        }

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0); // when size not known

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write("{}");
            writer.close();

            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }



            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            } else {
            }

        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
    }
}

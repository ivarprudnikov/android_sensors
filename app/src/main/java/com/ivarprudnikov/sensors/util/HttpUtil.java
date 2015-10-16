package com.ivarprudnikov.sensors.util;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;

import java.io.IOException;
import java.util.Map;

public class HttpUtil {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = AndroidJsonFactory.getDefaultInstance();

    public static HttpResponse post(String urlString, Map data, final Map<String, String> headers) throws IOException {

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                        combineHeaders(request.getHeaders(), headers);
                    }
                });
        GenericUrl url = new GenericUrl(urlString);
        JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, data);
        HttpRequest request = requestFactory.buildPostRequest(url, content);

        return request.execute();
    }

    private static HttpHeaders combineHeaders(HttpHeaders existing, Map<String, String> headers) {
        HttpHeaders h;
        if(existing != null){
            h = existing;
        } else {
            h = new HttpHeaders();
        }
        if(headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                h.put(entry.getKey(), entry.getValue());
            }
        }

        return h;
    }

}

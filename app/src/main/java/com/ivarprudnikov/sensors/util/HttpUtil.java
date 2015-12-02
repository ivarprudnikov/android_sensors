/**
 * This file is part of com.ivarprudnikov.sensors package.
 *
 * com.ivarprudnikov.sensors is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * com.ivarprudnikov.sensors is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.ivarprudnikov.sensors.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivarprudnikov.sensors.util;

import android.os.Build;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.DefaultConnectionFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class HttpUtil {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = getJsonfactoryInstance();

    public static JsonFactory getJsonfactoryInstance(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // only for honeycomb and newer versions
            return AndroidJsonFactory.getDefaultInstance();
        } else {
            return GsonFactory.getDefaultInstance();
        }
    }

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

    public static HttpResponse postWithCert(String urlString, Map data, final Map<String, String> headers, byte[] certificate) throws IOException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = new ByteArrayInputStream(certificate);
        keyStore.load(is, null);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, null);
        KeyManager[] keyManagers = kmf.getKeyManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, null, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        NetHttpTransport.Builder transportBuilder = new NetHttpTransport.Builder();
        NetHttpTransport transport = transportBuilder.setSslSocketFactory(sslSocketFactory).build();

        HttpRequestFactory requestFactory =
                transport.createRequestFactory(new HttpRequestInitializer() {
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

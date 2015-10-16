package com.ivarprudnikov.sensors.model;

import java.util.Map;

public class ExportCommand {

    String url;
    Map data;

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

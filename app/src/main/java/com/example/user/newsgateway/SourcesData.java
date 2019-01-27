package com.example.user.newsgateway;

import java.io.Serializable;

/**
 * Created by user on 22-04-2018.
 */

public class SourcesData implements Serializable {

    private String source_id;
    private String source_name;
    private String source_url;
    private String source_category;

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getSource_category() {
        return source_category;
    }

    public void setSource_category(String source_category) {
        this.source_category = source_category;
    }

    @Override
    public String toString() {
        return "SourcesData{" +
                "source_id='" + source_id + '\'' +
                ", source_name='" + source_name + '\'' +
                ", source_url='" + source_url + '\'' +
                ", source_category='" + source_category + '\'' +
                '}';
    }
}

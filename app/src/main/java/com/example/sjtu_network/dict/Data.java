package com.example.sjtu_network.dict;

import java.util.List;

public class Data {

/**
 * Copyright 2021 bejson.com
 */

private List<Entries> entries;
    private String query;
    private String language;
    private String type;
    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }
    public List<Entries> getEntries() {
        return entries;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    public String getQuery() {
        return query;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    public String getLanguage() {
        return language;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

}

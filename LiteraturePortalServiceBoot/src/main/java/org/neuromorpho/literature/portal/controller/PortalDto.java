package org.neuromorpho.literature.portal.controller;


public class PortalDto implements java.io.Serializable {

    private String name;
    private String url;
    private String base;
    private String db;
    private Integer searchPeriod;
    private String apiUrl;
    private String apiUrl2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSearchPeriod() {
        return searchPeriod;
    }

    public void setSearchPeriod(Integer searchPeriod) {
        this.searchPeriod = searchPeriod;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl2() {
        return apiUrl2;
    }

    public void setApiUrl2(String apiUrl2) {
        this.apiUrl2 = apiUrl2;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
    
}

package org.neuromorpho.literature.search.model.portal;

public class Portal {

    private String name;
    private String url;
    private String base;
    private Integer searchPeriod;
    private Boolean active;
    private String db;
    private String apiUrl;
    private String apiUrl2;

    public Portal(String name) {
        this.name = name;
    }

    public Portal() {
    }

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

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Integer getSearchPeriod() {
        return searchPeriod;
    }

    public void setSearchPeriod(Integer searchPeriod) {
        this.searchPeriod = searchPeriod;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
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

    public Boolean hasAPI() {
        return apiUrl != null;
    }

}

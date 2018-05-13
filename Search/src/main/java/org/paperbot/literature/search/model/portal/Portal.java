package org.paperbot.literature.search.model.portal;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "portal")
public class Portal {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String url;
    private String base;
    private Date startSearchDate;
    private Boolean active;
    private String db;
    private String apiUrl;
    private String token;

    public Portal(String name) {
        this.name = name;
    }

    public Portal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getStartSearchDate() {
        if (this.startSearchDate == null) {
            startSearchDate = new Date();
        }
        return startSearchDate;
    }

    public void setStartSearchDate(Date startSearchDate) {
        this.startSearchDate = startSearchDate;
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

    public Boolean hasAPI() {
        return apiUrl != null;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

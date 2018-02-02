package org.neuromorpho.literature.download.communication;


public class Article implements java.io.Serializable {

    private String id;
    private String doi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

}

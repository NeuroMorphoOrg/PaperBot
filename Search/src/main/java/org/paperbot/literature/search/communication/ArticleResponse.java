
package org.paperbot.literature.search.communication;


public class ArticleResponse implements java.io.Serializable{

    private String id;

    public ArticleResponse() {
    }

    public ArticleResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
 
}

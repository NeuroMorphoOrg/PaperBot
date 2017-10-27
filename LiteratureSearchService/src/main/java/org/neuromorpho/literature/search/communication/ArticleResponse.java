/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.communication;


/**
 *
 * @author wt
 */
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

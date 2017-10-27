/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.model.article;

import java.util.ArrayList;
import java.util.List;

public class Search implements java.io.Serializable {

    private String link;
    private String source;
    private String keyWord;
    private List<String> supplementaryLink;

    public Search() {
    }

    public Search(String source, String keyWord) {
        this.source = source;
        this.keyWord = keyWord;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<String> getSupplementaryLink() {
        return supplementaryLink;
    }

    public void setSupplementaryLink(List<String> supplementaryLink) {
        this.supplementaryLink = supplementaryLink;
    }

    public void setSupplementaryLink(String supplementaryLink) {
        if (this.supplementaryLink == null) {
            this.supplementaryLink = new ArrayList();
        }
        this.supplementaryLink.add(supplementaryLink);

    }

    @Override
    public String toString() {
        return "Search{" + "link=" + link + ", source=" + source + ", keyWord=" 
                + keyWord + ", supplementaryLink=" + supplementaryLink + '}';
    }
    
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller;

import java.util.List;
import org.neuromorpho.literature.model.article.PortalResults;

public class SearchPortalDto implements java.io.Serializable {

    private String link;
    private String source;
    private List<PortalResults> portalList;

    public SearchPortalDto() {
    }

    public SearchPortalDto(String doi, String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<PortalResults> getPortalList() {
        return portalList;
    }

    public void setPortalList(List<PortalResults> portalList) {
        this.portalList = portalList;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}

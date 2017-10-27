/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.model.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchPortal {

    private List<PortalResults> portalList;
    private String link;
    private List<String> supplementaryLink;
    private String source;
    private String pdf;
    private List<String> supplementaryPdf;

    public SearchPortal() {
    }

    public SearchPortal(String source) {
        this.source = source;

    }

    public SearchPortal(List<PortalResults> portalList, String link, String source) {
        this.portalList = portalList;
        this.link = link;
        this.source = source;
    }

    public SearchPortal(String portalName, String keyWord, String link) {
        this.source = portalName;
        if (!this.source.equals("manual")) {
            this.portalList = new ArrayList();
            List<String> keyWordList = new ArrayList();
            keyWordList.add(keyWord);
            PortalResults portal = new PortalResults(portalName, keyWordList);
            this.portalList.add(portal);
        }
    }

    public List<PortalResults> getPortalList() {
        return portalList;
    }

    public void setPortalList(List<PortalResults> portalList) {
        this.portalList = portalList;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public void updateSearchPortal(SearchPortal searchPortal, String keyWord) {
        //Create a structure to deal with duplicated
        Map<String, Set<String>> map = new HashMap<>();
        if (this.portalList != null) {
            for (PortalResults portalResults : this.portalList) {
                map.put(portalResults.getName(), portalResults.getKeyWordSet());
            }
            Set<String> keyWordSet = new HashSet<>();
            if (map.containsKey(searchPortal.getSource())) {
                keyWordSet = map.get(searchPortal.getSource());
            }
            keyWordSet.add(keyWord);
            map.put(searchPortal.getSource(), keyWordSet);

            this.portalList = new ArrayList();
            for (String key : map.keySet()) {
                List<String> KeyWordList = new ArrayList<>(map.get(key));
                this.portalList.add(new PortalResults(key, KeyWordList));
            }
        } else {
            createPortalResult(searchPortal.getSource(), keyWord);
        }
        if (this.link == null){
            this.link = searchPortal.getLink();
        }
    }

    public void createPortalResult(String portalName, String keyWord) {
        this.portalList = new ArrayList();
        this.portalList.add(new PortalResults(portalName, keyWord));
    }

    public List<String> getSupplementaryPdf() {
        return supplementaryPdf;
    }

    public void setSupplementaryPdf(List<String> supplementaryPdf) {
        this.supplementaryPdf = supplementaryPdf;
    }

}

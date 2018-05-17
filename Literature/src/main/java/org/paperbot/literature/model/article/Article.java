/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.model.article;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class Article implements java.io.Serializable{

    @Id
    private String id;

    //pmid is index if exists, but can be NULL
    @Indexed(unique = true, sparse = true)
    private String pmid;

    @Indexed(unique = true, sparse = true)
    private String doi;
    private String link;

    private String journal;

    private String title;
    private Date ocDate;
    private Date publishedDate;

    private List<Author> authorList;

    private List<Portal> searchPortal;

    private Date evaluatedDate;
    private String status;
    private List<String> dataUsage;

    public Article() {
    }

    public Article(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getOcDate() {
        return ocDate;
    }

    public void setOcDate(Date ocDate) {
        this.ocDate = ocDate;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public List<Portal> getSearchPortal() {
        return searchPortal;
    }

    public void setSearchPortal(List<Portal> searchPortal) {
        this.searchPortal = searchPortal;
    }


    public void setEvaluatedDateToday() {
        this.evaluatedDate = new Date();
    }

    public Date getEvaluatedDate() {
        return evaluatedDate;
    }

    public void setEvaluatedDate(Date evaluatedDate) {
        this.evaluatedDate = evaluatedDate;
    }

    public List<String> getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(List<String> dataUsage) {
        this.dataUsage = dataUsage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//
//    public Boolean hasContactEmail(List<Author> authorList) {
//        for (Author author : this.authorList) {
//            if (author.hasContactEmail()) {
//                return Boolean.TRUE;
//            }
//        }
//        return Boolean.FALSE;
//    }
//
    public void updateSearchPortal(String portalName, String keyWord) {
        //Create a structure to deal with duplicated
        Map<String, Set<String>> map = new HashMap<>();
        if (this.searchPortal != null) {
            for (Portal portal : this.searchPortal) {
                map.put(portal.getName(), portal.getKeyWordSet());
            }
            Set<String> keyWordSet = new HashSet<>();
            keyWordSet.add(keyWord);
            map.put(portalName, keyWordSet);

            this.searchPortal = new ArrayList();
            for (String key : map.keySet()) {
                List<String> KeyWordList = new ArrayList<>(map.get(key));
                this.searchPortal.add(new Portal(key, KeyWordList));
            }
        } else {
            this.searchPortal = new ArrayList();
            this.searchPortal.add(new Portal(portalName, keyWord));
        }

    }

    @Override
    public String toString() {
        return "Article{" + "id=" + id + ", pmid=" + pmid + ", doi=" + doi 
                + ", link=" + link + ", journal=" + journal + ", title=" 
                + title + ", ocDate=" + ocDate + ", publishedDate=" + publishedDate 
                + ", authorList=" + authorList + ", searchPortal=" + searchPortal 
                + ", evaluatedDate=" + evaluatedDate + ", status=" + status 
                + ", dataUsage=" + dataUsage + '}';
    }
    
    

}

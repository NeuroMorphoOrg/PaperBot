/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.model.article;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "neuromorpho")
public class NeuroMorphoArticle {

    @Id
    private ObjectId id;

    //pmid is index if exists, but can be NULL
    @Indexed(unique = true, sparse = true)
    private Integer pmid;

    @Indexed(unique = true, sparse = true)
    private String doi;

    private String journal;

    private String title;
    private Date ocDate;
    private Date publishedDate;

    private SearchPortal searchPortal;

    @PersistenceConstructor
    public NeuroMorphoArticle() {
    }

    public NeuroMorphoArticle(Integer pmid) {
        this.pmid = pmid;
    }

    public NeuroMorphoArticle(String title) {
        this.title = title;
        this.ocDate = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    @PersistenceConstructor
    public NeuroMorphoArticle(Integer pmid, String doi, String journal, String title,Date ocDate, Date publishedDate, SearchPortal searchPortal) {
        this.pmid = pmid;
        this.doi = doi;
        this.journal = journal;
        this.title = title;
        this.ocDate = ocDate;
        this.publishedDate = publishedDate;
        this.searchPortal = searchPortal;
    }

    public Integer getPmid() {
        return pmid;
    }

    public void setPmid(Integer pmid) {
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

    public SearchPortal getSearchPortal() {
        return searchPortal;
    }

    public void setSearchPortal(SearchPortal searchPortal) {
        this.searchPortal = searchPortal;
    }

}

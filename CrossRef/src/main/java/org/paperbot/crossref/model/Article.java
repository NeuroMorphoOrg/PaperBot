/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.crossref.model;

import java.util.Date;
import java.util.List;

public class Article {

  
    private String pmid;
    private String doi;
    private String journal;
    private String title;
    private Date ocDate;
    private Date publishedDate;
    private List<Author> authorList;
    private String pdfLink;

   
    public Article() {
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

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    @Override
    public String toString() {
        return "Article{" + "pmid=" + pmid + ", doi=" + doi + ", journal=" + journal 
                + ", title=" + title + ", ocDate=" + ocDate + ", publishedDate=" + publishedDate 
                + ", authorList=" + authorList + ", pdfLink=" + pdfLink + '}';
    }

}

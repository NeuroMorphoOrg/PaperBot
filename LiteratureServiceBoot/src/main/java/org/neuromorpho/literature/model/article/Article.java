/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.model.article;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "article.positives")
public class Article {

    @Id
    private ObjectId id;

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

    private SearchPortal searchPortal;

    private Date evaluatedDate;
    private String status;
    private List<String> dataUsage;

    public Article() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public SearchPortal getSearchPortal() {
        return searchPortal;
    }

    public void setSearchPortal(SearchPortal searchPortal) {
        this.searchPortal = searchPortal;
    }

    public void createSearchPortal(SearchPortal searchPortal) {
        this.searchPortal = searchPortal;
    }

//    public void updateSearchPortal(String portalName, String keyWord) {
//        if (this.searchPortal == null) {
//            this.searchPortal = new SearchPortal();
//        }
//        this.searchPortal.updateSearchPortal(portalName, keyWord);
//    }
    public Article getArticle() {
        return this;
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

    public Boolean isDoiNull() {
        return this.doi == null;
    }

    public Boolean isPMIDNull() {
        return this.pmid == null;
    }

    public Boolean isJournalNull() {
        return this.journal == null;
    }

    public Boolean isPublishedDateNull() {
        return this.doi == null;
    }

    public void mergeAuthorData(List<Author> newAuthorList) {
        if (this.authorList.size() < newAuthorList.size()) {
            this.authorList = newAuthorList;
        } else {
            for (Integer i = 0; i < newAuthorList.size(); i++) {
                this.authorList.get(i).mergeAuthorName(newAuthorList.get(i));
                this.authorList.get(i).mergeAuthorEmail(newAuthorList.get(i));
            }
        }

    }

    public void updateSearchPortal(SearchPortal searchPortal, String keyWord) {
        if (this.searchPortal == null) {
            this.searchPortal = new SearchPortal();
        }
        this.searchPortal.updateSearchPortal(searchPortal, keyWord);
    }

    
    public Boolean hasContactEmail(List<Author> authorList) {
        for (Author author : this.authorList) {
            if (author.hasContactEmail()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}

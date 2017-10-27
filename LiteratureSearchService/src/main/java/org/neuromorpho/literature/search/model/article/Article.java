package org.neuromorpho.literature.search.model.article;

import java.util.Date;
import java.util.List;

public class Article implements java.io.Serializable {

    private String id;
    private String pmid;
    private String title;
    private String journal;
    private String doi;
    private Date publishedDate;
    private List<Author> authorList;

    public Article(String id) {
        this.id = id;
    }

    public Article() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
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

    @Override
    public String toString() {
        String authorListStr = "";
        for (Author author: this.authorList){
            authorListStr = authorListStr + ", " + author.toString();
        }
        return "Article{" + "id=" + id + ", pmid=" + pmid + ", title=" + title
                + ", journal=" + journal + ", doi=" + doi + ", publishedDate="
                + publishedDate + ", authorList=" + authorList.toString() + '}';
    }

}

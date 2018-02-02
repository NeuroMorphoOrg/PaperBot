package org.neuromorpho.literature.download.communication;



import java.util.List;

public class ArticlePage implements java.io.Serializable {

    private List<Article> content;
    private Integer totalPages;
    private Integer totalElements;
    private Boolean last;
    private Boolean first;

    public List<Article> getContent() {
        return content;
    }

    public void setContent(List<Article> content) {
        this.content = content;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }
   

}

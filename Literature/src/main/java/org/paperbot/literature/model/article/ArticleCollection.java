package org.paperbot.literature.model.article;

import org.springframework.data.domain.Page;

public class ArticleCollection {

    public enum ArticleStatus {
        All("all", "All"),
        TO_EVALUATE("article", "Pending evaluation"),
        POSITIVE("article.positives", "Positive"),
        NEGATIVE("article.negatives", "Negative"),
        EVALUATED("article.evaluated", "Evaluated"),
        INACCESSIBLE("article.inaccessible", "Inaccessible"),
        NEUROMORPHO("article.neuromorpho", "Neuromorpho");

        private final String collection;
        private final String status;

        private ArticleStatus(String s, String a) {
            collection = s;
            status = a;
        }

        public static ArticleStatus getArticleStatus(String value) {
            for (ArticleStatus v : values()) {
                if (v.getStatus().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException(value);
        }

        public Boolean isPositive() {
            return this.equals(ArticleStatus.POSITIVE);
        }

        public Boolean isNegative() {
            return this.equals(ArticleStatus.NEGATIVE);
        }

        public Boolean isInaccessible() {
            return this.equals(ArticleStatus.INACCESSIBLE);
        }

        public String getCollection() {
            return this.collection;
        }

        public String getStatus() {
            return this.status;
        }

    }

    private Page<Article> articlePage;
    private String status;

    public ArticleCollection(Page<Article> articlePage, String status) {
        this.articlePage = articlePage;
        this.status = status;
    }

    public Page<Article> getArticlePage() {
        return articlePage;
    }

    public void setArticlePage(Page<Article> articlePage) {
        this.articlePage = articlePage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

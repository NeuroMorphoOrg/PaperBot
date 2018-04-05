package org.neuromorpho.literature.model.article;

public class ArticleCollection {

    public enum ArticleStatus {
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

    private Article article;
    private ArticleStatus articleStatus;

    public ArticleCollection(Article article, ArticleStatus articleStatus) {
        this.article = article;
        this.articleStatus = articleStatus;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public ArticleStatus getArticleStatus() {
        return articleStatus;
    }

    public void setArticleStatus(ArticleStatus articleStatus) {
        this.articleStatus = articleStatus;
    }

}

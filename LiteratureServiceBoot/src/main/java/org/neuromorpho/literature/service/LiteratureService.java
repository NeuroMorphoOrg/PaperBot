package org.neuromorpho.literature.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.ArticleCollection.ArticleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.neuromorpho.literature.repository.article.ArticleRepositoryExtended;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class LiteratureService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArticleRepositoryExtended articleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Long> getSummary(Date date) {
        return articleRepository.getSummary(date);
    }

    public Page<Article> getArticleList(String collection, Map<String, String> fieldQuery, Integer page) {
        return articleRepository.findByFieldQuery(collection, fieldQuery, page);
    }

    public Page<Article> getArticles(String text, ArticleStatus articleStatus, 
            Integer page, String sortDirection, String sortProperty) {
        log.debug("Find article list from collection: " + articleStatus.getCollection() 
                + " by text: " + text + " sortDirection: " + sortDirection 
                + " sortProperty: " + sortProperty);
        return articleRepository.findByText(text, articleStatus, page, sortDirection, sortProperty);
    }

    public String saveArticle(ArticleCollection article) {
        return articleRepository.save(article);

    }

    public void deleteArticle(String id) {
        articleRepository.delete(id);
    }

    public ArticleCollection findArticle(String id) {
        log.debug("Reading article id: " + id);
        return articleRepository.findById(id);
    }

    public ArticleCollection findArticleByPmid(String pmid) {
        log.debug("Reading article id: " + pmid);
        return articleRepository.findByPMID(pmid);
    }

    public void replaceArticle(String id, Article article) {
        log.debug("Updating fields for : " + id);
        articleRepository.replace(id, article);
    }

    public void updateArticle(String id, Map<String, Object> article) {
        log.debug("Updating fields for : " + id);
        articleRepository.update(id, article);
    }

    public void updateCollection(String id, ArticleStatus newCollection) {
        log.debug("Updating collection for : " + id + " to: " + newCollection);
        articleRepository.update(id, newCollection);
    }

    public void deleteArticleList(List<String> ids) {
        log.debug("Removing articles from DB:" + ids);
        for (String id : ids) {
            articleRepository.delete(id);
        }
    }

    public void deleteArticleList(String collection) {
        if (collection == null) {
            log.debug("Removing all articles from DB");

            for (ArticleStatus status : ArticleStatus.values()) {
                mongoTemplate.remove(new Query(), status.getCollection());
            }
        } else {
            log.debug("Removing all articles from DB for collection: " + collection);
            mongoTemplate.remove(new Query(), ArticleStatus.getArticleStatus(collection).getCollection());
        }

    }

}

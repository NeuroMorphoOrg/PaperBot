package org.paperbot.literature.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.paperbot.literature.model.article.Article;
import org.paperbot.literature.model.article.ArticleCollection;
import org.paperbot.literature.model.article.ArticleCollection.ArticleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.paperbot.literature.repository.article.ArticleRepositoryExtended;
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
    
    public ArticleCollection getArticles(ArticleStatus articleStatus,
            Map<String, String> queryParams) {
        log.debug("Find article list from status: " + articleStatus.getCollection());
        return articleRepository.findByText(articleStatus, queryParams);
    }
    
    public String saveArticle(Article article, ArticleStatus status) {
        String id = articleRepository.saveOrUpdate(article, status);
        return id;
    }
    
    public void deleteArticle(String id) {
        articleRepository.delete(id);
    }
    
    public ArticleCollection findArticle(String id) {
        log.debug("Reading article id: " + id);
        return articleRepository.findById(id);
    }
    
    public void updateArticle(ArticleStatus status, String id, Article article, Boolean update) {
        log.debug("Updating fields for : " + id + " update: " + update);
        if (update) {
            articleRepository.update(status, id, article);
        } else {
            articleRepository.save(article, status);
            
        }
    }
    
    public void updateStatus(String id, ArticleStatus oldStatus, ArticleStatus newStatus) {
        log.debug("Updating status for : " + id + " to: " + newStatus);
        articleRepository.update(id, oldStatus, newStatus);
    }
    
    public void deleteArticleList(List<String> ids) {
        log.debug("Removing articles from DB:" + ids);
        for (String id : ids) {
            articleRepository.delete(id);
        }
    }
    
    public void deleteArticleList(String status) {
        if (status == null) {
            log.debug("Removing all articles from DB");
            for (ArticleStatus articleStatus : ArticleStatus.values()) {
                mongoTemplate.remove(new Query(), articleStatus.getCollection());
            }
        } else {
            log.debug("Removing all articles from DB for status: " + status);
            mongoTemplate.remove(new Query(), ArticleStatus.getArticleStatus(status).getCollection());
        }
        
    }
    
}

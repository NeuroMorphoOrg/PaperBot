package org.neuromorpho.literature.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.ArticleCollection.ArticleStatus;
import org.neuromorpho.literature.repository.article.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class LiteratureService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArticleRepository articleRepository;

   
    public Map<String, Long> getSummary(Date date) {
        return articleRepository.getSummary(date);
    }

    public List<String> getFieldValues(String field) {
        return articleRepository.findFieldValues(field);
    }

    public Page<Article> getArticles(Map<String, List<String>> fieldQuery, Integer page) {
        return articleRepository.findByFieldQuery(fieldQuery, page);
    }

    public Page<Article> getArticles(String text, ArticleStatus articleStatus, Integer page) {
        return articleRepository.findByText(text, articleStatus, page);
    }

    public String  saveArticle(ArticleCollection article) {
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

    public void update(String id, Article article) {
        log.debug("Updating fields for : " + id);
        articleRepository.update(id, article);
    }
    
    public void updateCollection(String id, ArticleStatus newCollection) {
        log.debug("Updating collection for : " + id + " to: " + newCollection);
        articleRepository.update(id, newCollection);
    }


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.repository.article;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.ArticleCollection.ArticleStatus;
import org.neuromorpho.literature.model.article.SearchPortal;
import org.springframework.data.domain.Page;

public interface ArticleRepository {

    public Map<String, Long> getSummary(Date date);

    public ArticleCollection findById(String id);

    public String save(ArticleCollection article);
    
    public String saveOrUpdate(ArticleCollection article);

    public void delete(String id);

    public ArticleCollection existsArticle(Article article);

    public void update(String id, ArticleStatus newCollection);

    public void update(String id, Map<String, Object> article);

    public void replace(String id, Article article);
    
    public void update(String id, SearchPortal searchPortal, String keyWord);

    public Page<Article> findByFieldQuery(String collection, Map<String, String> fieldQuery, Integer pageStart);

    public Page<Article> findByText(String text, ArticleStatus status, Integer pageStart);

    public ArticleCollection findByPMID(String pmid);

}

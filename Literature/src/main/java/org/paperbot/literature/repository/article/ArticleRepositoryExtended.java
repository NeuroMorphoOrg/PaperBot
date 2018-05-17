/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.repository.article;

import java.util.Date;
import java.util.Map;
import org.paperbot.literature.model.article.Article;
import org.paperbot.literature.model.article.ArticleCollection;
import org.paperbot.literature.model.article.ArticleCollection.ArticleStatus;
import org.springframework.data.domain.Page;

public interface ArticleRepositoryExtended {

    public Map<String, Long> getSummary(Date date);

    public ArticleCollection findById(String id);

    public String save(Article article, ArticleStatus status);
    
    public String saveOrUpdate(Article article, ArticleStatus status);

    public void delete(String id);

    public ArticleCollection existsArticle(Article article);

    public void update(String id, ArticleStatus oldCollection, ArticleStatus newCollection);

    public void update(ArticleStatus status, String id, Article article);
    
    public void update(String id, String portalName, String keyWord);

    public Page<Article> findByFieldQuery(String collection, Map<String, String> fieldQuery, Integer pageStart);

    public ArticleCollection findByText(String text, ArticleStatus status, Integer pageStart, String sortDirection, String sortProperty);


}

package org.neuromorpho.literature.service.search;

import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.SearchPortal;
import org.neuromorpho.literature.repository.article.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArticleRepository articleRepository;

    public String saveOrUpdateArticle(ArticleCollection article) {
        String id = articleRepository.saveOrUpdate(article);
        return id;
    }

    public void updateSearch(String id, SearchPortal searchPortal, String keyWord) {
        log.debug("Updating search for article: " + id);
        articleRepository.update(id, searchPortal, keyWord);
    }
}

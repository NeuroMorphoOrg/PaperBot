package org.neuromorpho.literature.service.search;

import org.neuromorpho.literature.model.article.ArticleCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.neuromorpho.literature.repository.article.ArticleRepositoryExtended;

@Service
public class SearchService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArticleRepositoryExtended articleRepository;

    public String saveOrUpdateArticle(ArticleCollection article) {
        String id = articleRepository.saveOrUpdate(article);
        return id;
    }

    public void updateSearch(String id, String portalName, String keyWord) {
        log.debug("Updating search for article: " + id);
        articleRepository.update(id, portalName, keyWord);
    }
}

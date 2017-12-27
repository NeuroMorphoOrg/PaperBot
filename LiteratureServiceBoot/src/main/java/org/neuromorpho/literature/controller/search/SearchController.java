package org.neuromorpho.literature.controller.search;

import org.neuromorpho.literature.controller.ArticleDto;
import org.neuromorpho.literature.controller.ArticleDtoAssembler;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.service.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/search")
public class SearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SearchService searchService;

    private final SearchDtoAssembler searchDtoAssembler = new SearchDtoAssembler();
    private final ArticleDtoAssembler articleDtoAssembler = new ArticleDtoAssembler();

    @RequestMapping(value = "/{articleStatus}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto saveOrUpdateArticle(
            @PathVariable String articleStatus,
            @RequestBody ArticleDto article) {
        Article articleTeated = articleDtoAssembler.createArticle(article);

        String _id = searchService.saveOrUpdateArticle(
                new ArticleCollection(articleTeated, ArticleCollection.ArticleStatus.getArticleStatus(articleStatus)));
        return new ArticleDto(_id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateSearch(
            @PathVariable String id,
            @RequestBody SearchDto search) {
        searchService.updateSearch(id, searchDtoAssembler.createSearch(search), search.getKeyWord());
    }

}

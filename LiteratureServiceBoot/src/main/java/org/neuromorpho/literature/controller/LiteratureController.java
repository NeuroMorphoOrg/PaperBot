package org.neuromorpho.literature.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.neuromorpho.literature.exceptions.DuplicatedException;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.ArticleCollection.ArticleStatus;
import org.neuromorpho.literature.service.LiteratureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature")
public class LiteratureController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LiteratureService literatureService;

    private final FieldsAssembler fieldsAssembler = new FieldsAssembler();

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "Literature up & running!";
    }

    @CrossOrigin
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Map<String, Long> getSummary(
            @RequestParam(required = false) String date) {
        return literatureService.getSummary(fieldsAssembler.getFirstDay(date));
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Article findArticle(@PathVariable String id) {
        ArticleCollection article = literatureService.findArticle(id);
        return article.getArticle();
    }
//
//    @CrossOrigin
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void replaceArticle(
//            @PathVariable String id,
//            @RequestBody Article article) {
//        log.debug("Replacing article: " + article.toString());
//        literatureService.replaceArticle(id, article);
//    }

    @CrossOrigin
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateArticle(
            @PathVariable String id,
            @RequestBody Map<String, Object> article) {
        log.debug("Updating article id: " + id + " with data: " + article.toString());
        literatureService.updateArticle(id, article);
    }

    @CrossOrigin
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Page<Article> getArticles(
            @RequestParam(required = true) String collection,
            @RequestParam(required = false) Map<String, String> keyValueMap,
            @RequestParam(required = true) Integer page) {
        log.debug("Find articles by query collection : " + collection + " and page: " + page);
        keyValueMap.remove("collection");
        keyValueMap.remove("page");
        Page<Article> articlePage = literatureService.getArticleList(
                collection, keyValueMap, page);
        log.debug("Found #articles : " + articlePage.getTotalElements());

        return articlePage;
    }

    @CrossOrigin
    @RequestMapping(value = "/{articleStatus}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Article createArticle(
            @PathVariable String articleStatus,
            @RequestBody Article article,
            @RequestParam(required = false) Boolean update) {
        if (update == null){
            update = Boolean.FALSE;
        }
        String _id = literatureService.saveArticle(
                new ArticleCollection(article, ArticleStatus.getArticleStatus(articleStatus)), update);
        return new Article(_id);
    }

    @CrossOrigin
    @RequestMapping(value = "status/{status}", method = RequestMethod.GET)
    public Page<Article> getArticles(
            @PathVariable String status,
            @RequestParam(required = false) String text,
            @RequestParam(required = true) Integer page,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortProperty) {
        log.debug("Find articles by text collection : " + status + " and page: " + page);
        ArticleStatus articleStatus = ArticleStatus.getArticleStatus(status);
        Page<Article> articlePage = literatureService.getArticles(
                text, articleStatus, page, sortDirection, sortProperty);
        return articlePage;
    }

    @CrossOrigin
    @RequestMapping(value = "objectId", method = RequestMethod.GET)
    public @ResponseBody
    IdDto getNewObjectId() {
        return new IdDto(new ObjectId().toString());
    }

    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Article findArticleByPMID(
            @RequestParam(required = false) String pmid) {
        ArticleCollection article = literatureService.findArticleByPmid(pmid);
        return article.getArticle();
    }

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody
    Map<String, Object> handleDuplicatedException(DuplicatedException e,
            HttpServletRequest request,
            HttpServletResponse resp) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("errorMessage", e.getMessage());
        return result;
    }

    @CrossOrigin
    @RequestMapping(value = "/collection/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateCollection(
            @PathVariable String id,
            @RequestParam String articleStatus) {
        literatureService.updateCollection(id, ArticleCollection.ArticleStatus.getArticleStatus(articleStatus));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteArticleList(@RequestParam List<String> ids) {
        literatureService.deleteArticleList(ids);

    }

    @CrossOrigin
    @RequestMapping(value = "/removeAll", method = RequestMethod.DELETE)
    public void deleteArticleList(@RequestParam(required = false) String collection) {
        literatureService.deleteArticleList(collection);
    }

}

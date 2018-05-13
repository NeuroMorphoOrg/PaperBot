package org.paperbot.literature.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.paperbot.literature.exceptions.DuplicatedException;
import org.paperbot.literature.model.article.Article;
import org.paperbot.literature.model.article.ArticleCollection;
import org.paperbot.literature.model.article.ArticleCollection.ArticleStatus;
import org.paperbot.literature.service.LiteratureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping()
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
    public ArticleCollection findArticle(@PathVariable String id) {
        ArticleCollection article = literatureService.findArticle(id);
        return article;
    }


    @CrossOrigin
    @RequestMapping(value = "/{status}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Article createArticle(
            @PathVariable String status,
            @RequestBody Article article) {
        String _id = literatureService.saveArticle(article, ArticleStatus.getArticleStatus(status));
        return new Article(_id);
    }

    @CrossOrigin
    @RequestMapping(value = "/{status}/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateArticle(
            @PathVariable String status,
            @PathVariable String id,
            @RequestBody Article article,
            @RequestParam Boolean update) {
        log.debug("Updating article id: " + id + " with data: " + article.toString());
        literatureService.updateArticle(ArticleStatus.getArticleStatus(status), id, article, update);
    }

    @CrossOrigin
    @RequestMapping(value = "status/{status}", method = RequestMethod.GET)
    public ArticleCollection getArticles(
            @PathVariable String status,
            @RequestParam(required = false) String text,
            @RequestParam(required = true) Integer page,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortProperty) {
        log.debug("Find articles by text status : " + status + " and page: " + page);
        ArticleStatus articleStatus = ArticleStatus.getArticleStatus(status);
        ArticleCollection articleCollection = literatureService.getArticles(
                text, articleStatus, page, sortDirection, sortProperty);
        log.debug("Found #articles : " + articleCollection.getArticlePage().getTotalElements());

        return articleCollection;
    }

    @CrossOrigin
    @RequestMapping(value = "objectId", method = RequestMethod.GET)
    public @ResponseBody
    IdDto getNewObjectId() {
        return new IdDto(new ObjectId().toString());
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
    @RequestMapping(value = "/status/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStatus(
            @PathVariable String id,
            @RequestParam String articleStatus) {
        literatureService.updateStatus(id, ArticleCollection.ArticleStatus.getArticleStatus(articleStatus));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteArticleList(@RequestParam List<String> ids) {
        literatureService.deleteArticleList(ids);

    }

    @CrossOrigin
    @RequestMapping(value = "/removeAll", method = RequestMethod.DELETE)
    public void deleteArticleList(@RequestParam(required = false) String status) {
        literatureService.deleteArticleList(status);
    }

}

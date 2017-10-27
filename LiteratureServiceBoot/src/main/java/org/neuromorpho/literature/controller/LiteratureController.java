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

    private final ArticleDtoAssembler articleDtoAssembler = new ArticleDtoAssembler();
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
    public ArticleDto findArticle(@PathVariable String id) {
        ArticleCollection article = literatureService.findArticle(id);
        return articleDtoAssembler.createArticleDto(article);
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateArticle(
            @PathVariable String id,
            @RequestBody ArticleDto article) {
        log.debug("Updating article: " + article.toString());
        literatureService.update(id, articleDtoAssembler.createArticle(article));
    }

    @CrossOrigin
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Page<ArticleDto> getArticles(
            @RequestParam(required = false) String publishedYear,
            @RequestParam(required = false) String ocYear,
            @RequestParam(required = false) String pmid,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String usage,
            @RequestParam(required = false) String ltDate,
            @RequestParam(required = false) String gtDate,
            @RequestParam(required = false) String email,
            @RequestParam(required = true) String articleStatus,
            @RequestParam(required = true) Integer page) {
        Map<String, List<String>> keyValueMap = fieldsAssembler.getFieldQuery(
                publishedYear, ocYear, ltDate, gtDate,
                pmid, species, usage, email, articleStatus);

        Page<Article> articlePage = literatureService.getArticles(
                keyValueMap, page);
        List<ArticleDto> articleDtoList = new ArrayList();
        for (Article article : articlePage) {
            ArticleCollection articleCollection = new ArticleCollection(article, null);
            articleDtoList.add(articleDtoAssembler.createArticleDto(articleCollection));
        }
        return new PageImpl<ArticleDto>(articleDtoList, new PageRequest(page,
                articlePage.getSize(), articlePage.getSort()), articlePage.getTotalElements());
    }

    @CrossOrigin
    @RequestMapping(value = "/{articleStatus}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto createArticle(
            @PathVariable String articleStatus,
            @RequestBody ArticleDto article) {
        String _id = literatureService.saveArticle(articleDtoAssembler.createArticle(article, articleStatus));
        return new ArticleDto(_id);
    }
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteArticle(
            @RequestBody String id) {
        throw new UnsupportedOperationException("Operation not permited");
//        literatureService.deleteArticle(id);
    }
    
    @CrossOrigin
    @RequestMapping(value = "status/{status}", method = RequestMethod.GET)
    public Page<ArticleDto> getArticles(
            @PathVariable String status,
            @RequestParam(required = false) String text,
            @RequestParam(required = true) Integer page) {
        ArticleStatus articleStatus = ArticleStatus.getArticleStatus(status);
        Page<Article> articlePage = literatureService.getArticles(
                text, articleStatus, page);
        List<ArticleDto> articleDtoList = new ArrayList();
        for (Article article : articlePage) {
            ArticleCollection articleCollection = new ArticleCollection(article, articleStatus);
            articleDtoList.add(articleDtoAssembler.createArticleDto(articleCollection));
        }
        return new PageImpl<ArticleDto>(articleDtoList, new PageRequest(page,
                articlePage.getSize(), articlePage.getSort()), articlePage.getTotalElements());
    }
    
    @CrossOrigin
    @RequestMapping(value = "objectId", method = RequestMethod.GET)
    public @ResponseBody
    IdDto getNewObjectId() {
        return new IdDto(new ObjectId().toString());
    }
    
    
    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ArticleDto findArticleByPMID(
            @RequestParam(required = false) String pmid) {
        ArticleCollection article = literatureService.findArticleByPmid(pmid);
        return articleDtoAssembler.createArticleDto(article);
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.repository.article;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.paperbot.literature.exceptions.DuplicatedException;
import org.paperbot.literature.model.article.Article;
import org.paperbot.literature.model.article.ArticleCollection;
import org.paperbot.literature.model.article.ArticleCollection.ArticleStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Update;

@Repository
public class ArticleRepositoryExtendedImpl implements ArticleRepositoryExtended {


    private final Integer pageSize = 50;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MongoTemplate mongoOperations;

    @Override
    public String save(Article article, ArticleStatus status) {
        log.debug("Saving new article: " + status.getCollection());
        ArticleCollection oldArticle = this.existsArticle(article);
        if (oldArticle != null) {
            throw new DuplicatedException(oldArticle.getStatus());
        }
        if (!status.equals(ArticleStatus.TO_EVALUATE)) {
            article.setEvaluatedDateToday();
        }

        article.setOcDate(new Date());
        mongoOperations.save(article, status.getCollection());
        log.debug("Article saved: " + article.getId());
        return article.getId();
    }

    @Override
    public String saveOrUpdate(Article article, ArticleStatus status) {
        ArticleCollection oldArticle = this.existsArticle(article);
        String id;
        if (oldArticle != null) {
            id = oldArticle.getArticlePage().getContent().get(0).getId();
            log.debug("Updating article with id: " + id);
            Article old = oldArticle.getArticlePage().getContent().get(0);
            Article newArticle = article;
            // if new article has more data than saved article update
            Query query = new Query(Criteria.where("_id").is(old.getId()));
            Update update = new Update();
            Boolean needUpdate = Boolean.FALSE;
            if (newArticle.getDoi() != null && old.getDoi() == null) {
                update.set("doi", newArticle.getDoi());
                needUpdate = Boolean.TRUE;
            }
            if (newArticle.getPmid() != null && old.getPmid() == null) {
                update.set("pmid", newArticle.getPmid());
                needUpdate = Boolean.TRUE;

            }
            if (newArticle.getPublishedDate() != null && old.getPublishedDate() == null) {
                update.set("publishedDate", newArticle.getPublishedDate());
                needUpdate = Boolean.TRUE;

            }
            if (newArticle.getJournal() != null && old.getJournal() == null) {
                update.set("journal", newArticle.getJournal());
                needUpdate = Boolean.TRUE;

            }
            if ((newArticle.getAuthorList() != null || newArticle.getAuthorList().size() > 0)
                    && (old.getAuthorList() == null || old.getAuthorList().isEmpty())) {
                update.set("authorList", newArticle.getAuthorList());
                needUpdate = Boolean.TRUE;

            }
            if (needUpdate) {
                mongoOperations.updateFirst(query, update, ArticleStatus.getArticleStatus(oldArticle.getStatus()).getCollection());
            }

        } else {
            article.setOcDate(new Date());
            mongoOperations.save(article, status.getCollection());
            id = article.getId();
            log.debug("Saving new article with id: " + id);

        }
        return id;
    }

    @Override
    public Map<String, Long> getSummary(Date date) {
        Map<String, Long> articlesNumbers = new HashMap();
        if (date != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("ocDate").lt(date));
            Long articles = mongoOperations.count(query, ArticleStatus.TO_EVALUATE.getCollection());
            articlesNumbers.put("old", articles);

            query = new Query();
            query.addCriteria(Criteria.where("ocDate").gte(date));
            articles = mongoOperations.count(query, ArticleStatus.TO_EVALUATE.getCollection());
            articlesNumbers.put("new", articles);
        }
        for (ArticleStatus status : ArticleStatus.values()) {
            Long articles = mongoOperations.count(null, status.getCollection());
            articlesNumbers.put(status.getStatus(), articles);
        }
        // get distict values for dataUsage
        String collection = ArticleStatus.POSITIVE.getCollection();
        List<String> valueList = mongoOperations.getCollection(collection).distinct("dataUsage");
        for (String value : valueList) {
            Query query = new Query();
            query.addCriteria(Criteria.where("dataUsage").is(value));
            Long articles = mongoOperations.count(query, collection);
            articlesNumbers.put(value, articles);
        }
        return articlesNumbers;
    }

    @Override
    public void delete(String id) {
        for (ArticleStatus status : ArticleStatus.values()) {
            Article article = mongoOperations.findById(id, Article.class, status.getCollection());
            if (article != null) {
                log.debug("Removing article: " + id + " from collection: " + status.getCollection());
                mongoOperations.remove(article, status.getCollection());
            }
        }

    }

    @Override
    public void update(String id, ArticleStatus oldCollection, ArticleStatus newCollection) {
        Article article = mongoOperations.findById(id, Article.class, oldCollection.getCollection());
        if (article != null){
            log.debug("Updating collection article: " + article.toString());
            if (oldCollection.equals(ArticleStatus.TO_EVALUATE)) {
                article.setEvaluatedDate(new Date());
            }
            log.debug("Saving article in new collection: " + newCollection.getCollection());
            mongoOperations.save(article, newCollection.getCollection());
            log.debug("Removing article from old collection: " + oldCollection.getCollection());
            mongoOperations.remove(article, oldCollection.getCollection());
        }
    }


//    @Override
//    public void update(String id, Map<String, Object> article) {
//        ArticleCollection oldArticle = findById(id);
//        if (oldArticle != null) {
//            Query query = new Query();
//            query.addCriteria(Criteria.where("_id").is(id));
//            Update update = new Update();
//            for (Map.Entry<String, Object> entry : article.entrySet()) {
//                update.set(entry.getKey(), entry.getValue());
//            }
//            mongoOperations.updateFirst(query, update, ArticleStatus.valueOf(oldArticle.getStatus()).getCollection());
//        }
//
//    }

    @Override
    public void update(String id, String portalName, String keyWord) {
        ArticleCollection oldArticle = this.findById(id);
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();

        oldArticle.getArticlePage().getContent().get(0).updateSearchPortal(portalName, keyWord);
        update.set("searchPortal", oldArticle.getArticlePage().getContent().get(0).getSearchPortal());
        mongoOperations.updateFirst(query, update, ArticleStatus.getArticleStatus(oldArticle.getStatus()).getCollection());

    }

    @Override
    public void update(ArticleStatus status, String id, Article article) {
        mongoOperations.save(article, status.getCollection());

    }
    /*
    * Search in all the collections for a given article
     */
    @Override
    public ArticleCollection existsArticle(Article article) {
        for (ArticleStatus status : ArticleStatus.values()) {
            Article duplicatedArticle = this.existsArticleInCollection(article, status.getCollection());
            if (duplicatedArticle != null) {
                List<Article> articleList = new ArrayList();
                articleList.add(duplicatedArticle);
                Page<Article> articlePage = new PageImpl<>(articleList);
                return new ArticleCollection(articlePage, status.getStatus());
            }
        }
        return null;

    }

    private Article existsArticleInCollection(Article article, String collection) {

        //exact title
        Article duplicatedExactTitle = mongoOperations.findOne(Query.query(Criteria.where("title").is(article.getTitle())),
                Article.class, collection);
        if (duplicatedExactTitle != null) {
            return duplicatedExactTitle;
        }

        //in case no exact title but contains pmid
        if (article.getPmid() != null && !article.getPmid().isEmpty()) {
            Article duplicatePmid = mongoOperations.findOne(Query.query(Criteria.where("pmid").is(article.getPmid())),
                    Article.class, collection);
            if (duplicatePmid != null) {
                return duplicatePmid;

            }
        }
        //in case no exact title but contains doi
        if (article.getDoi() != null && !article.getDoi().isEmpty()) {
            Article duplicateDoi = mongoOperations.findOne(Query.query(Criteria.where("doi").is(article.getDoi())),
                    Article.class, collection);
            if (duplicateDoi != null) {
                return duplicateDoi;
            }

        }
        //similar title only for the ones without pmid & or doi
        if ((article.getPmid() == null || article.getPmid().isEmpty())
                && (article.getDoi() == null || article.getDoi().isEmpty())) {
            List<Article> duplicateTitleList = mongoOperations.findAll(Article.class, collection);
            for (Article articleTitle : duplicateTitleList) {
                JaroWinklerDistance jwDistance = new JaroWinklerDistance();
                Float distance = jwDistance.getDistance(article.getTitle(), articleTitle.getTitle());
                if (distance > 0.8) {
                    return articleTitle;
                }
            }
        }

        return null;
    }

    /*
    * Find article in all the DB by id
     */
    @Override
    public ArticleCollection findById(String id) {
        ArticleCollection articleCollection = null;
        for (ArticleStatus status : ArticleStatus.values()) {
            Article article = this.existsArticleByIdInCollection(id, status.getCollection());
            if (article != null) {
                List<Article> articleList = new ArrayList();
                articleList.add(article);
                Page<Article> articlePage = new PageImpl<>(articleList);
                articleCollection = new ArticleCollection(articlePage, status.getStatus());
            }
        }
        return articleCollection;

    }

    private Article existsArticleByIdInCollection(String id, String collection) {
        Article duplicate = mongoOperations.findById(id, Article.class, collection);
        return duplicate;
    }

    @Override
    public ArticleCollection findByText(String text,
            ArticleStatus status,
            Integer pageStart,
            String sortDirection,
            String sortProperty) {
        if (sortDirection == null || sortProperty == null) {
            sortDirection = "DESC";
            sortProperty = "publishedDate";
        }
        Sort sort = new Sort(Sort.Direction.valueOf(sortDirection), sortProperty);
        PageRequest pageRequest = new PageRequest(pageStart, pageSize);

        Query query = new Query();

        //search in title, doi, pmid, authors
        if (text != null && !text.isEmpty()) {
            Map<String, String> pair = new HashMap();
            pair.put("pmid", text);
            pair.put("doi", text);
            pair.put("title", text);
            pair.put("authorList.name", text);
            pair.put("authorList.email", text);
            Criteria criteriaOr = getOrCriteriaList(pair);
            query.addCriteria(criteriaOr);
        }
        query.with(sort);
        query.skip(pageStart * pageSize);
        query.limit(pageSize);
        List<Article> articleList = mongoOperations.find(query, Article.class, status.getCollection());

        Long n = mongoOperations.count(query, Article.class, status.getCollection());
        Page<Article> articlePage = new PageImpl<>(articleList, pageRequest, n);

        return new ArticleCollection(articlePage, status.getStatus());

    }

    @Override
    public Page<Article> findByFieldQuery(
            String collection,
            Map<String, String> fieldQuery,
            Integer pageStart) {
        PageRequest pageRequest = new PageRequest(pageStart, pageSize);
        Query query = new Query();
        for (Map.Entry pair : fieldQuery.entrySet()) {
            log.debug("Adding Filter to query:" + pair);
            if (pair.getValue().toString().contains("$exist")) {
                String exists = pair.getValue().toString().split(":")[1];
                query.addCriteria(Criteria.where(pair.getKey().toString()).exists(Boolean.parseBoolean(exists)));
            } else {
//                Criteria criteriaOr = getOrCriteriaListExactMatch(pair);
//                query.addCriteria(criteriaOr);
                query.addCriteria(Criteria.where(pair.getKey().toString()).is(pair.getValue().toString()));
            }
        }

        query.with(
                new Sort(Sort.Direction.DESC, "publishedDate"));
        query.skip(pageStart
                * pageSize);
        query.limit(pageSize);

        String status = ArticleStatus.getArticleStatus(collection).getCollection();
        List<Article> articleList = mongoOperations.find(query, Article.class, status);

        Long n = mongoOperations.count(query, Article.class, status);
        Page<Article> articlePage = new PageImpl<>(articleList, pageRequest, n);
        return articlePage;

    }

    private Criteria getOrCriteriaList(Map<String, String> pairMap) {
        ArrayList<Criteria> criteriaList = new ArrayList();
        Criteria criteria = new Criteria();
        for (Map.Entry<String, String> entry : pairMap.entrySet()) {
            if (entry.getKey().equals("title")
                    || entry.getKey().equals("authorList.name")
                    || entry.getKey().equals("authorList.email")) {
                criteria = Criteria.where(entry.getKey()).
                        regex(Pattern.compile(entry.getValue(), Pattern.CASE_INSENSITIVE));
            } else {
                criteria = Criteria.where(entry.getKey()).is(entry.getValue());
            }
            criteriaList.add(criteria);
            criteria = new Criteria();
        }

        if (pairMap.size() > 1) {
            Criteria[] criteriaArr = new Criteria[criteriaList.size()];
            criteria.orOperator(criteriaList.toArray(criteriaArr));
        } else {
            criteria = criteriaList.get(0);
        }
        return criteria;
    }

    private Criteria getOrCriteriaList(Map.Entry pair) {
        ArrayList<Criteria> criteriaList = new ArrayList();
        Criteria criteria = new Criteria();
        for (String pairString : (List<String>) pair.getValue()) {
            if (pair.getKey().equals("publishedDate")) {
                criteria = Criteria.where(pair.getKey().toString())
                        .gte(this.getStartYearDate(pairString))
                        .lte(this.getEndYearDate(pairString));
            } else if (pair.getKey().equals("ltDate")) {
                criteria = Criteria.where("ocDate")
                        .lt(this.getFirstDay(pairString));
            } else if (pair.getKey().equals("gtDate")) {
                criteria = Criteria.where("ocDate")
                        .gte(this.getFirstDay(pairString));
            } else {
                criteria = Criteria.where(pair.getKey().toString()).regex(pairString);
            }
            criteriaList.add(criteria);
            criteria = new Criteria();
        }
        List<String> orValues = (List<String>) pair.getValue();

        if (orValues.size()
                > 1) {
            Criteria[] criteriaArr = new Criteria[criteriaList.size()];
            criteria.orOperator(criteriaList.toArray(criteriaArr));
        } else {
            criteria = criteriaList.get(0);
        }
        return criteria;
    }

    private Criteria getOrCriteriaListExactMatch(Map.Entry pair) {
        ArrayList<Criteria> criteriaList = new ArrayList();
        Criteria criteria = new Criteria();
        for (String pairString : (List<String>) pair.getValue()) {
            if (pair.getKey().equals("publishedDate")) {
                criteria = Criteria.where(pair.getKey().toString())
                        .gte(this.getStartYearDate(pairString))
                        .lte(this.getEndYearDate(pairString));
            } else if (pair.getKey().equals("ltDate")) {
                criteria = Criteria.where("ocDate")
                        .lt(this.getFirstDay(pairString));
            } else if (pair.getKey().equals("gtDate")) {
                criteria = Criteria.where("ocDate")
                        .gte(this.getFirstDay(pairString));
            } else {
                criteria = Criteria.where(pair.getKey().toString()).is(pairString);
            }
            criteriaList.add(criteria);
            criteria = new Criteria();
        }
        List<String> orValues = (List<String>) pair.getValue();

        if (orValues.size()
                > 1) {
            Criteria[] criteriaArr = new Criteria[criteriaList.size()];
            criteria.orOperator(criteriaList.toArray(criteriaArr));
        } else {
            criteria = criteriaList.get(0);
        }
        return criteria;
    }

    private Boolean isNumeric(String s) {
        return java.util.regex.Pattern.matches("\\d+", s);
    }

    private Date getStartYearDate(String year) {
        Calendar date = Calendar.getInstance();   //current date
        date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND));
        date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE));
        date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY));
        date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.MONTH, date.getActualMinimum(Calendar.MONTH));
        date.set(Calendar.YEAR, Integer.parseInt(year));
        return date.getTime();
    }

    private Date getEndYearDate(String year) {
        Calendar date = Calendar.getInstance();   //current date
        date.set(Calendar.SECOND, date.getActualMaximum(Calendar.SECOND));
        date.set(Calendar.MINUTE, date.getActualMaximum(Calendar.MINUTE));
        date.set(Calendar.HOUR_OF_DAY, date.getActualMaximum(Calendar.HOUR_OF_DAY));
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.MONTH, date.getActualMaximum(Calendar.MONTH));
        date.set(Calendar.YEAR, Integer.parseInt(year));
        return date.getTime();
    }

    public Date getFirstDay(String dateStr) {
        Date result = null;
        if (dateStr != null) {
            String[] dateListStr = dateStr.split("-");

            Calendar date = Calendar.getInstance();   //current date
            date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND));
            date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE));
            date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY));
            date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
            date.set(Calendar.MONTH, Integer.parseInt(dateListStr[1]) - 1);
            date.set(Calendar.YEAR, Integer.parseInt(dateListStr[0]));
            result = date.getTime();
        }
        return result;
    }


}

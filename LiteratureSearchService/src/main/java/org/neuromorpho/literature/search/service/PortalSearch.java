package org.neuromorpho.literature.search.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.model.article.Search;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.neuromorpho.literature.search.communication.LiteratureConnection;
import org.neuromorpho.literature.search.communication.PubMedConnection;
import org.neuromorpho.literature.search.model.article.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class PortalSearch implements IPortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Search searchPortal;
    protected Document searchDoc;
    protected Document articleDoc;
    protected Portal portal;
    protected String keyWord;
    protected String collection;
    protected Date startDate;
    protected Date endDate;
    protected Integer searchPeriod;
    protected Boolean inaccessible;
    protected Article article;

    @Autowired
    protected LiteratureConnection literatureConnection;
    @Autowired
    protected PubMedConnection pubMedConnection;

    @Override
    public void findArticleList(KeyWord keyWord, Portal portal) {
        try {
            this.portal = portal;
            this.keyWord = keyWord.getName();
            this.searchPeriod = portal.getSearchPeriod();
            this.startDate = this.getSearchStartDate();
            this.endDate = this.getSearchEndDate();
            this.searchPortal = new Search(portal.getName(), keyWord.getName());
            this.collection = keyWord.getCollection();

            log.debug("Executing portal " + portal.getName() + " for keyword " + keyWord.getName());
            if (this.portal.hasAPI()) {
                this.searchForTitlesApi();
            } else {
                this.searchPage();
                this.searchForTitles();
            }

        } catch (IOException ex) { // if jsour returns this exception, the page was empty
            log.debug("Aticles found 0 ");
        } catch (Exception ex) {
            log.error("Exception " + this.portal.getName(), ex);
        }

    }

    //to be override by the sons
    protected abstract Elements findArticleList();

    protected abstract void searchPage() throws IOException;

    protected abstract Boolean loadNextPage();

    protected abstract String fillTitle(Element articleData);

    protected abstract void fillPublishedDate(Element articleData, Element articlePage);

    protected abstract void fillJournal(Element articleData, Element articlePage);

    protected abstract void fillAuthorList(Element articleData, Element articlePage);

    protected abstract void fillDoi(Element articleData, Element articlePage);

    protected abstract void fillLinks(Element articleData, Element articlePage);

    protected abstract void fillIsAccessible(Element articleData, Element articlePage);

    protected void searchForTitles() throws Exception {
        try {

            Elements articleList = this.findArticleList();
            for (Element articleElement : articleList) {
                this.createArticle(articleElement);
            }
            Boolean existsNextPage = this.loadNextPage();
            if (existsNextPage) {
                searchForTitles();
            }

        } catch (Exception ex) {
            log.error("Exception: ", ex);
        }
    }

    protected void createArticle(Element articleData) {
        this.inaccessible = Boolean.FALSE;
        this.article = new Article();
        Integer i = 0;
        Boolean read = Boolean.FALSE;
        try {
            while (i < 10 && !read) {
                String articleLink = this.fillTitle(articleData);
                if (!this.article.getTitle().isEmpty()
                        && !containsHanScript(this.article.getTitle())) {
                    log.debug("Reading article: " + articleLink);
                    Element articlePage = null;
                    log.debug("Article title: " + this.article.getTitle());
                    log.debug("Article link: " + articleLink);
                    if (articleLink != null && !this.portal.getName().equals("GoogleScholar")) {
                        articleDoc = Jsoup.connect(articleLink)
                                .timeout(30 * 1000)
                                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36").get();
                        articlePage = articleDoc.select("html").first();
                    }
                    this.fillIsAccessible(articleData, articlePage);

                    this.fillJournal(articleData, articlePage);
                    this.fillAuthorList(articleData, articlePage);
                    this.fillPublishedDate(articleData, articlePage);
                    this.fillDoi(articleData, articlePage);
                    if (!this.inaccessible) {
                        this.fillLinks(articleData, articlePage);
                    }
                    //call pubmed to retrieve pubmedID
                    String pmid = pubMedConnection.findTitleFromPMID(this.article.getTitle(), "pubmed");
                    if (pmid == null) {
                        pmid = pubMedConnection.findTitleFromPMID(this.article.getTitle(), "pmc");
                    }
                    if (pmid != null) {
                        this.article.setPmid(pmid);
                    }
                    log.debug(this.article.toString());
                    // calling rest to save the article & updating the portal search values
                    ArticleResponse response = literatureConnection.saveArticle(this.article, this.inaccessible, this.collection);
                    log.debug(this.searchPortal.toString());

                    literatureConnection.saveSearchPortal(response.getId(), this.searchPortal);

                }
                read = Boolean.TRUE;
            }
        } catch (SocketTimeoutException ex) {
            i++;
            log.warn("Timeout exception number: " + i + " for article: " + this.article.getTitle());
        } catch (IOException ex) {
            log.error("Exception for article: " + this.article.getTitle(), ex);
        }
    }

    protected Date getSearchStartDate() {
        Calendar date = Calendar.getInstance();   //current date
        date.add(Calendar.MONTH, -(this.searchPeriod));
        date.set(Calendar.DAY_OF_MONTH, 1);
        return date.getTime();
    }

    protected Date getSearchEndDate() {
        Calendar date = Calendar.getInstance();   //current date
        return date.getTime();
    }

    protected Date tryParseDate(String dateStr) {
        String[] formatStrings = {"dd MMM yyyy", "dd MMMMM yyyy", "MMMMM yyyy", "yyyy", "yyyy/MM/dd", "yyyy-MM-dd", "MMM yyyy", "yyyy-MM"};
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString, Locale.US).parse(dateStr.toLowerCase());
            } catch (ParseException e) {
            }
        }
        return null;
    }

    /*
    * Contains Asian characters
     */
    private static Boolean containsHanScript(String s) {
        for (int i = 0; i < s.length();) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    protected abstract void searchForTitlesApi();

}

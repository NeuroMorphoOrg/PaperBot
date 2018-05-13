package org.paperbot.literature.search.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.paperbot.literature.search.communication.ArticleResponse;
import org.paperbot.literature.search.communication.CrossRefConnection;
import org.paperbot.literature.search.model.portal.KeyWord;
import org.paperbot.literature.search.model.portal.Portal;
import org.paperbot.literature.search.communication.LiteratureConnection;
import org.paperbot.literature.search.communication.PubMedConnection;
import org.paperbot.literature.search.model.article.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class PortalSearch implements IPortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Document searchDoc;
    protected Document articleDoc;
    protected Portal portal;
    protected String keyWord;
    protected String collection;
    protected Date startDate;
    protected Date endDate;
    protected Boolean inaccessible;
    protected Article article;

    @Autowired
    protected LiteratureConnection literatureConnection;
    @Autowired
    protected PubMedConnection pubMedConnection;

    @Autowired
    protected CrossRefConnection crossRefConnection;

    @Override
    public void findArticleList(KeyWord keyWord, Portal portal) throws InterruptedException, HttpStatusException {
        try {
            this.portal = portal;
            this.keyWord = keyWord.getName();
            this.startDate = portal.getStartSearchDate();
            this.endDate = this.getSearchEndDate();
            this.collection = keyWord.getCollection();

            log.debug("Executing portal " + portal.getName() + " for keyword " + keyWord.getName());
            if (this.portal.hasAPI()) {
                this.searchForTitlesApi();
            } else {
                this.searchPage();
                this.searchForTitles();
            }
        } catch (HttpStatusException ex) { // if jsour returns this exception, the page was empty
            log.error("Error", ex);
            throw ex;

        } catch (IOException ex) { // if jsour returns this exception, the page was empty
            log.debug("Articles found 0 ");
            log.error("Error", ex);
        }

    }

    //to be override by the sons
    protected abstract Elements findArticleList();

    protected abstract void searchPage() throws IOException;

    protected abstract Boolean loadNextPage() throws InterruptedException;

    protected abstract String fillTitle(Element articleData);

    protected abstract void fillPublishedDate(Element articleData, Element articlePage);

    protected abstract void fillJournal(Element articleData, Element articlePage);

    protected abstract void fillAuthorList(Element articleData, Element articlePage);

    protected abstract void fillDoi(Element articleData, Element articlePage);

    protected abstract void fillLinks(Element articleData, Element articlePage);

    protected void searchForTitles() throws HttpStatusException, InterruptedException {

        Elements articleList = this.findArticleList();
        for (Element articleElement : articleList) {
            this.createArticle(articleElement);
        }
        Boolean existsNextPage = this.loadNextPage();
        if (existsNextPage) {
            searchForTitles();
        }

    }

    protected void createArticle(Element articleData) throws InterruptedException {
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

                    this.fillJournal(articleData, articlePage);
                    this.fillAuthorList(articleData, articlePage);
                    this.fillPublishedDate(articleData, articlePage);
                    this.fillDoi(articleData, articlePage);
                    if (!this.inaccessible) {
                        this.fillLinks(articleData, articlePage);
                    }
                    String pmid = pubMedConnection.findPMIDFromTitle(this.article.getTitle());
                    this.saveArticle(pmid);
                }
                read = Boolean.TRUE;
            }
        } catch (SocketTimeoutException ex) {
            i++;
            log.warn("Timeout exception number: " + i + " for article: " + this.article.getTitle());
        } catch (IOException | NullPointerException ex) {
            log.error("Exception for article: " + this.article.getTitle(), ex);
        }
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

    protected abstract void searchForTitlesApi() throws InterruptedException;

    protected void saveArticle(String pmid) throws InterruptedException {
        if (pmid != null) {
            this.article = pubMedConnection.findArticleFromPMID(pmid);
        }
        if (this.article.getPublishedDate() != null && this.article.getPublishedDate().after(this.portal.getStartSearchDate())) {
            log.debug(this.article.toString());
            ArticleResponse response = literatureConnection.saveArticle(
                    this.article, "Inaccessible");

            literatureConnection.saveSearchPortal(response.getId(), this.portal.getName(), this.keyWord);
            Boolean exists = Boolean.FALSE;
            if (article.getDoi() != null) {
                exists = crossRefConnection.downloadPDF(article.getDoi(), response.getId());
            }
            if (exists) {
                literatureConnection.updateCollection(
                        response.getId(), this.collection);
            }
            log.debug("Seconds to sleep: " + 2);
            log.debug("......................................");
            Thread.sleep(2 * 1000);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }

    }

}

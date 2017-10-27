package org.neuromorpho.literature.search.service;

import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Search;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.communication.LiteratureConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;

@Service
public abstract class PortalSearch implements IPortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Search searchPortal;
    protected Document searchDoc;
    protected Document articleDoc;
    protected Portal portal;
    protected String keyWord;
    protected String collection;
    protected Integer numArticlesTotal;
    protected Date startDate;
    protected Date endDate;
    protected Integer searchPeriod;
    protected Article article;
    protected Boolean inaccessible;
    protected Integer i = 0;

    @Autowired
    protected LiteratureConnection literatureConnection;

    @Override
    public Integer findArticleList(KeyWord keyWord, Portal portal) {
        try {
            this.numArticlesTotal = 0;
            this.portal = portal;
            this.keyWord = keyWord.getName();
            this.searchPeriod = portal.getSearchPeriod();
            this.startDate = this.getSearchStartDate();
            this.endDate = this.getSearchEndDate();
            this.searchPortal = new Search(portal.getName(), keyWord.getName());
            this.collection = keyWord.getCollection();

            log.debug("Executing portal " + portal.getName() + " for keyword " + keyWord.getName());
            if (i < 10) {
                if (this.portal.hasAPI()) {
                    this.searchForTitlesApi();
                } else {

                    Integer numPages = 1;
                    this.searchPage();
                    this.searchForTitles(numPages);
                  
                }
            }

        } catch (Exception ex) {
            log.error("Exception " + this.portal.getName(), ex);
        }

        log.debug(this.portal.getName() + " found for keyWord: " + keyWord.getName() + " " + numArticlesTotal);

        return numArticlesTotal;

    }

    //to be override by the sons
    protected abstract Elements findArticleList();

    protected abstract void searchForTitlesApi();

    protected abstract void searchPage();

    protected abstract Boolean loadNextPage();

    protected abstract String fillTitle(Element articleData);

    protected abstract void fillPublishedDate(Element articleData, Element articlePage);

    protected abstract void fillJournal(Element articleData, Element articlePage);

    protected abstract void fillAuthorList(Element articleData, Element articlePage);

    protected abstract void fillDoi(Element articleData, Element articlePage);

    protected abstract void fillLinks(Element articleData, Element articlePage);

    protected abstract void fillIsAccessible(Element articleData, Element articlePage);

    protected void searchForTitles(Integer numPages) throws Exception {
        try {

            Elements articleList = this.findArticleList();
            for (Element articleElement : articleList) {
                this.createArticle(articleElement);
            }
              Boolean existsNextPage = this.loadNextPage();
            if (existsNextPage) {
                log.debug("Reading page number: " + numPages);
                numPages++;
                searchForTitles(numPages);
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
                    this.getPMIDFromTitle(this.article.getTitle(), "pubmed");
                    if (this.article.getPmid() == null) {
                        this.getPMIDFromTitle(this.article.getTitle(), "pmc");
                    }
                    log.debug(this.article.toString());
                   // calling rest to save the article & updating the portal search values
                    ArticleResponse response = literatureConnection.saveArticle
                                (this.article, this.inaccessible, this.collection);
                    log.debug(this.searchPortal.toString());

                    literatureConnection.saveSearchPortal(response.getId(), this.searchPortal);

                }
                read = Boolean.TRUE;
            }
        } catch (SocketTimeoutException ex) {
            i++;
            log.warn("Timeout exception number: " + i +" for article: " + this.article.getTitle());
        } catch (Exception ex) {
            read = Boolean.TRUE;
            log.error("Exception for article: " + this.article.getTitle(), ex);
        }
    }

    private Date getSearchStartDate() {
        Calendar date = Calendar.getInstance();   //current date
        date.add(Calendar.MONTH, -(this.searchPeriod));
        date.set(Calendar.DAY_OF_MONTH, 1);
        return date.getTime();
    }

    private Date getSearchEndDate() {
        Calendar date = Calendar.getInstance();   //current date
        return date.getTime();
    }

    protected Date tryParseDate(String dateStr) {
        String[] formatStrings = {"dd MMM yyyy", "dd MMMMM yyyy", "MMMMM yyyy", "yyyy", "yyyy/MM/dd", "yyyy-MM-dd", "MMM yyyy"};
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

    public void getPMIDFromTitle(String title, String database) throws Exception {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
        log.debug("Filling pmid from title ");
        RestTemplate restTemplate = new RestTemplate();

        String url = uri + "esearch.fcgi?"
                + "db=" + database
                + "&retmode=json"
                + "&term=" + title
                + "&field=title";
        Map<String, Object> pmidMap = restTemplate.getForObject(
                url,
                Map.class);
        log.debug("Accessing " + url);
        Map result = (HashMap) pmidMap.get("esearchresult");
        ArrayList<String> uidList = (ArrayList) result.get("idlist");

        for (String uid : uidList) {
            String xmlUri = uri + "esummary.fcgi?"
                    + "db=" + database
                    + "&retmode=xml"
                    + "&version=2.0"
                    + "&id=" + uid;
            log.debug("Accessing " + xmlUri);

            String articleXML = restTemplate.getForObject(
                    xmlUri,
                    String.class);
            String posibleTitle = this.getTitle(articleXML);
            JaroWinklerDistance jwDistance = new JaroWinklerDistance();
            Float distance = jwDistance.getDistance(posibleTitle, article.getTitle());
            log.debug("possible match title: " + posibleTitle
                    + " JaroWinklerDistance to the real title: " + distance);

            if (distance >= 0.9) {
                if (database.equals("pmc")) {
                    uid = "PMC" + uid;
                }
                article.setPmid(uid);
            }
        }

    }

    private String getTitle(String xml) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        InputSource source = new InputSource(new StringReader(xml));
        return xpath.evaluate("//Title", source);
    }
}

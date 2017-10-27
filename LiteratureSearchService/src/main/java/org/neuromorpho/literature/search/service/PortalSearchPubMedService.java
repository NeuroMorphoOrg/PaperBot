/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Author;
import org.neuromorpho.literature.search.model.article.Search;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PortalSearchPubMedService extends PortalSearch {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(PortalSearchPubMedService.class);

    @Override
    public void searchForTitlesApi() {

        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        RestTemplate restTemplate = new RestTemplate();
        String uri = this.portal.getApiUrl() + "/esearch.fcgi?"
                + "db=" + this.portal.getDb()
                + "&retmode=json"
                + "&retmax=5000"
                + "&term=" + this.keyWord
                + " AND " + yearFormat.format(this.startDate.getTime()) + ":"
                + yearFormat.format(this.endDate.getTime()) + "[pdat]";
        log.debug("PubMed retrieving from URI: " + uri);
        this.searchPortal = new Search(this.portal.getName(), this.keyWord);

        Map<String, Object> pmidMap = restTemplate.getForObject(uri, Map.class);

        Map result = (HashMap) pmidMap.get("esearchresult");
        ArrayList<String> uidList = (ArrayList) result.get("idlist");
        for (String uid : uidList) {
            try {
                this.inaccessible = Boolean.FALSE;
                this.article = new Article();
                log.debug("PMID: " + uid);
                this.article.setPmid(uid);

                retrieveArticleDataFromPMID();

                // update pmid for pmc
                if (this.portal.getDb().equals("pmc")) {
                    String pmid = this.getPMIDFromPMC(uid);
                    if (pmid != null) {
                        this.article.setPmid(pmid);
                    } else {
                        this.article.setPmid("PMC" + uid);
                    }
                }
                log.debug(this.article.toString());
                ArticleResponse response = literatureConnection.saveArticle(
                        this.article, this.inaccessible, this.collection);
                literatureConnection.saveSearchPortal(response.getId(), this.searchPortal);
//            

            } catch (Exception ex) {
                log.error("Exception: " + uri, ex);

            }
        }

    }

    public void retrieveArticleDataFromPMID() throws Exception {

        fillArticle(article, this.portal.getDb());

        fillLinks();
    }

    protected void fillArticle(Article article, String db) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        String pmid = article.getPmid().replace("PMC", "");
        String uri2 = this.portal.getApiUrl() + "/esummary.fcgi?"
                + "db=" + db
                + "&retmode=json"
                + "&version=2.0"
                + "&id=" + pmid;
        Map<String, Object> articleMap = restTemplate.getForObject(
                uri2,
                Map.class);

        Map result = (HashMap) articleMap.get("result");

        ArrayList<String> uids = (ArrayList) result.get("uids");
        if (uids.isEmpty()) {
            throw new Exception("Error Pubmed! unknown pmid: " + article.getPmid());
        }
        Map articleValues = (HashMap) result.get(uids.get(0));
        article.setTitle(getCorrectedName((String) articleValues.get("title")));
        article.setJournal(getCorrectedName((String) articleValues.get("fulljournalname")));
        String dateStr = (String) articleValues.get("pubdate");
        String sortDateStr = (String) articleValues.get("sortpubdate");
        if (sortDateStr == null) {
            sortDateStr = (String) articleValues.get("sortdate");
        }
        if (!sortDateStr.isEmpty()) {
            try {
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                article.setPublishedDate(format.parse(sortDateStr));
            } catch (ParseException ex) {
                throw new Exception("Error Pubmed! date error:" + dateStr + " for pmid: " + article.getPmid());
            }
        }

        ArrayList<HashMap> authorListMap = (ArrayList) articleValues.get("authors");

        List<Author> authorList = new ArrayList();
        for (HashMap authorMap : authorListMap) {
            authorList.add(fromMapAuthor(authorMap));
        }
        article.setAuthorList(authorList);

        ArrayList<HashMap> articleIds = (ArrayList) articleValues.get("articleids");
        for (HashMap articleIdMap : articleIds) {
            if (((String) articleIdMap.get("idtype")).equals("doi")) {
                article.setDoi((String) articleIdMap.get("value"));
            }
        }
    }

    private void fillLinks() {
//        if (this.portal.getDb().equals("pubmed")) {
        RestTemplate restTemplate = new RestTemplate();

        String uri3 = this.portal.getApiUrl() + "/elink.fcgi?"
                + "db=" + this.portal.getDb()
                + "&cmd=llinks"
                + "&id=" + article.getPmid();
        String xml = restTemplate.getForObject(
                uri3,
                String.class);

        log.debug("Reading article link from: " + uri3);
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        if (!doc.select("Url").isEmpty()) {
            this.searchPortal.setLink(doc.select("Url").first().text());
        }
//        } else {
//            String link = this.portal.getUrl() + "PMC" + this.article.getPmid();
//            this.driverArticle.get(link);
//            WebElement articlePage = driverArticle.findElement(By.xpath(".//html"));
//            this.fillLinks(null, articlePage);
//        }
    }

    public static Author fromMapAuthor(HashMap authorMap) {
        String[] completeName = ((String) authorMap.get("name")).split(" ");
        String name = completeName[1];
        if (completeName[1].length() == 2) {
            name = completeName[1].charAt(0) + ". " + completeName[1].charAt(1);
        }
        Author author = new Author(name + ". " + completeName[0], null);
        return author;
    }

    @Override
    protected void searchPage() {
        throw new UnsupportedOperationException("Not needed if accessing through API");
    }

//    @Override
//    /*
//    * Used to download the pdf from pubmedCentral
//     */
//    protected void fillLinks(WebElement articleData, WebElement articlePage) {
//        try {
//            //.format-menu li
//            WebElement pdfLink = articlePage.findElement(By.xpath(".//link[@type='application/pdf']"));
//            this.searchPortal.setLink(pdfLink.getAttribute("href"));
//
//            List<WebElement> supplementaryLinkElementList = articlePage.findElements(By.xpath(".//div[@class='sup-box half_rhythm']/a"));
//            log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
//            for (WebElement supplementaryLinkElement : supplementaryLinkElementList) {
//                String supplementaryLink = supplementaryLinkElement.getAttribute("href");
//                log.debug(supplementaryLink);
//                this.searchPortal.setSupplementaryLink(supplementaryLink);
//            }
//
//        } catch (NoSuchElementException e) {
//            log.error("No Link for article: " + article.getTitle());
//        }
//    }
//    @Override
//    protected void fillIsAccessible(WebElement articleData, WebElement articlePage) {
//        throw new UnsupportedOperationException("Not needed if accessing through API");
//    }
//
    public String getPMIDFromPMC(String uid) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> pmidFromPMCMap = restTemplate.getForObject(
                this.portal.getApiUrl2()
                + "ids=PMC" + uid
                + "&format=json",
                Map.class);
        ArrayList<Map<String, Object>> recordsList = (ArrayList) pmidFromPMCMap.get("records");
        return (String) recordsList.get(0).get("pmid");
    }

//    public void fillPMFromTitle(Article article, String database) throws Exception {
//        log.debug("Filling pmid from title ");
//        RestTemplate restTemplate = new RestTemplate();
//
//        String url = this.portal.getApiUrl() + "esearch.fcgi?"
//                + "db=" + database
//                + "&retmode=json"
//                + "&term=" + article.getTitle()
//                + "&field=title";
//        Map<String, Object> pmidMap = restTemplate.getForObject(
//                url,
//                Map.class);
//        log.debug("Accessing " + url);
//        Map result = (HashMap) pmidMap.get("esearchresult");
//        ArrayList<String> uidList = (ArrayList) result.get("idlist");
//
//        for (String uid : uidList) {
//            String xmlUri = this.portal.getApiUrl() + "esummary.fcgi?"
//                    + "db=" + database
//                    + "&retmode=xml"
//                    + "&version=2.0"
//                    + "&id=" + uid;
//            log.debug("Accessing " + xmlUri);
//
//            String articleXML = restTemplate.getForObject(
//                    xmlUri,
//                    String.class);
////            String posibleTitle = getTitle(articleXML);
//            JaroWinklerDistance jwDistance = new JaroWinklerDistance();
//            Float distance = jwDistance.getDistance(posibleTitle, article.getTitle());
//            log.debug("possible match title: " + posibleTitle
//                    + " JaroWinklerDistance to the real title: " + distance);
//
//            if (distance >= 0.9) {
//                if (database.equals("pmc")) {
//                    uid = "PMC" + uid;
//                }
//                article.setPmid(uid);
//            }
//        }
//
//    }
//    
    private static String getCorrectedName(String name) {
        String result = name.replace("&amp;", "&");
        return result.replace("&lt;i&gt;", "");
    }

    @Override
    protected Elements findArticleList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String fillTitle(Element articleData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Boolean loadNextPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

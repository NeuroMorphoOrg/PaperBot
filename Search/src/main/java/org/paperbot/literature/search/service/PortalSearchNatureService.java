/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.search.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.paperbot.literature.search.model.article.Article;
import org.paperbot.literature.search.model.article.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchNatureService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void searchForTitlesApi() throws InterruptedException {

        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        String keyWordTreated = this.keyWord.replace(" ", "+");
        String startRecord = "1";

        do {//iterate over pages
            String uri = this.portal.getApiUrl()
                    + "startRecord=" + startRecord
                    + "&queryType=cql&query=cql.keywords==" + keyWordTreated
                    + "+AND+prism.publicationDate=" + yearFormat.format(this.startDate.getTime());
            log.debug("API retrieving from URI: " + uri);
            String data;
            try {
                data = Jsoup.connect(uri).followRedirects(true).execute().body();
                Document result = Jsoup.parse(data, "", Parser.xmlParser());

                String total = result.select("numberOfRecords").text();
                if (total.isEmpty()) {
                    total = "0";
                }
                log.debug("Articles Found : " + total + " startRecord " + startRecord);

                startRecord = result.select("nextRecordPosition").text();
                for (Element record : result.select("recordData")) {
                    article = new Article();
                    String title1 = record.select("dc|title").text();
                    String title = Jsoup.parse(title1).text();
                    article.setTitle(title);
                    article.setDoi(record.select("prism|doi").text());
                    article.setJournal(record.select("prism|publicationName").text());
                    article.setLink(record.select("prism|url").text());

                    String dateStr = record.select("prism|publicationDate").text();
                    article.setPublishedDate(this.tryParseDate(dateStr));

                    List<Author> authorList = new ArrayList();

                    for (Element creator : record.select("dc|creator")) {
                        Author author = new Author(creator.text(), null);
                        authorList.add(author);

                    }
                    article.setAuthorList(authorList);
                    String pmid = pubMedConnection.findPMIDFromTitle(title);

                    this.saveArticle(pmid);

                }

            } catch (IOException ex) {
                log.error("Exception loading url", ex);
            }

        } while (!startRecord.isEmpty());

    }

    @Override
    protected Elements findArticleList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void searchPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Boolean loadNextPage() {
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
    protected void searchForTitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

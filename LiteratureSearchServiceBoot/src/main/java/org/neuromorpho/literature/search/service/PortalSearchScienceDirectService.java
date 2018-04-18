/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PortalSearchScienceDirectService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void searchForTitlesApi() throws InterruptedException {
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        RestTemplate restTemplate = new RestTemplate();

        Boolean next = Boolean.FALSE;
        String uri = this.portal.getApiUrl()
                + "query=" + this.keyWord
                + "&date=" + yearFormat.format(this.startDate.getTime())
                //+ "&start=" + startRecord
                + "&apiKey=" + this.portal.getToken();
        do {//iterate over pages

            log.debug("API retrieving from URI: " + uri);
            Map<String, Object> result = restTemplate.getForObject(uri, Map.class);

            Map nResultsMap = (Map) result.get("search-results");

            String totalStr = (String) nResultsMap.get("opensearch:totalResults");
            Integer total = Integer.parseInt(totalStr);
            log.debug("Articles Found : " + totalStr);

            ArrayList<Map> infoList = (ArrayList) nResultsMap.get("entry");
            if (total > 0) {
                for (Map info : infoList) {
                    article = new Article();
                    String title = (String) info.get("dc:title");
                    article.setTitle(title);
                    article.setDoi((String) info.get("prism:doi"));
                    article.setJournal((String) info.get("prism:publicationName"));
                    ArrayList<Map> urlListMap = (ArrayList) info.get("link");
                    article.setLink((String) urlListMap.get(1).get("@href"));

                    String dateStr = (String) info.get("prism:coverDisplayDate");

                    article.setPublishedDate(this.tryParseDate(dateStr));

                    Map authors = (HashMap) info.get("authors");
                    List<Author> authorList = new ArrayList();

                    if (authors != null) {
                        ArrayList<Map> authorListMap = (ArrayList) authors.get("author");

                        for (Map authorMap : authorListMap) {
                            String lastName = (String) authorMap.get("surname");
                            String firstName = (String) authorMap.get("given-name");

                            Author author = new Author(firstName + " " + lastName, null);
                            authorList.add(author);
                        }
                    }
                    article.setAuthorList(authorList);

                    //call pubmed to retrieve pubmedID
                    String pmid = pubMedConnection.findPMIDFromTitle(title);
                    this.saveArticle(pmid);

                }

            }
            ArrayList<Map> linkList = (ArrayList) nResultsMap.get("link");
            next = Boolean.FALSE;
            for (Map link : linkList) {
                String ref = (String) link.get("@ref");
                if (ref.equals("next")) {
                    uri = (String) link.get("@href");
                    next = Boolean.TRUE;
                }
            }

        } while (next);

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
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void searchForTitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

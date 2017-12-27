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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Author;
import org.neuromorpho.literature.search.model.article.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PortalSearchScienceDirectService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void searchForTitlesApi() {
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        RestTemplate restTemplate = new RestTemplate();
        this.searchPortal = new Search(this.portal.getName(), this.keyWord);

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
                    Article article = new Article();
                    String title = (String) info.get("dc:title");
                    article.setTitle(title);
                    article.setDoi((String) info.get("prism:doi"));
                    article.setJournal((String) info.get("prism:publicationName"));
                    ArrayList<Map> urlListMap = (ArrayList) info.get("link");
                    article.setLink((String) urlListMap.get(1).get("@href"));

                    String dateStr = (String) info.get("prism:coverDisplayDate");

                    article.setPublishedDate(tryParseDate(dateStr));

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

                    article.setAbstractText((String) info.get("prism:teaser"));

                    //call pubmed to retrieve pubmedID
                    String pmid = pubMedConnection.findTitleFromPMID(title, "pubmed");
                    if (pmid == null) {
                        pmid = pubMedConnection.findTitleFromPMID(title, "pmc");
                    }
                    if (pmid != null) {
                        article.setPmid(pmid);
                    }
                    log.debug(article.toString());

                    // calling rest to save the article & updating the portal search values
                    ArticleResponse response = literatureConnection.saveArticle(article, Boolean.FALSE, this.collection);
                    log.debug(searchPortal.toString());

                    literatureConnection.saveSearchPortal(response.getId(), this.searchPortal);
                }

                ArrayList<Map> linkList = (ArrayList) nResultsMap.get("link");
                for (Map link : linkList) {
                    String ref = (String) link.get("@ref");
                    if (ref.equals("next")) {
                        uri = (String) link.get("@href");
                        next = Boolean.TRUE;
                    }
                }
            }

        } while (next);

    }

    private Date tryParseDate(String dateString) {
        List<String> formatStrings = Arrays.asList("dd MMMM yyyy", "MMMM yyyy");

        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
            }
        }

        return null;
    }

}

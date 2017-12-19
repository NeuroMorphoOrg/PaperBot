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
import java.util.List;
import java.util.Map;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Author;
import org.neuromorpho.literature.search.model.article.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PortalSearchSpringerLinkService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${springerToken}")
    private String token;

    @Override
    public void searchForTitlesApi() {
        Integer page = 1;
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        RestTemplate restTemplate = new RestTemplate();

        /*
         * Springer incorrectly returning media type html inestead of json
         */
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        // Add JSON message handler
        MappingJackson2HttpMessageConverter json = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList();
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        // Add default media type in case marketplace uses incorrect MIME type, otherwise
        // Spring refuses to process it, even if its valid JSON
        json.setSupportedMediaTypes(supportedMediaTypes);
        mc.add(json);
        restTemplate.setMessageConverters(mc);
        /*
        * Springer incorrectly returning media type html inestead of json
         */
        Integer iterations = 1;
        do {//iterate over pages
            String uri = this.portal.getApiUrl()
                    + "q=(" + this.keyWord
                    + "AND year:" + yearFormat.format(this.startDate.getTime())
                    + ")&p=100&s=" + page
                    + "&api_key=" + this.token;
            log.debug("API retrieving from URI: " + uri);
            this.searchPortal = new Search(this.portal.getName(), this.keyWord);
            page++;
            Map<String, Object> result = restTemplate.getForObject(uri, Map.class);

            ArrayList<Map> nResultsMap = (ArrayList) result.get("result");
            String totalStr = (String) nResultsMap.get(0).get("total");
            Integer total = Integer.parseInt(totalStr);
            String pageLengthStr = (String) nResultsMap.get(0).get("pageLength");
            Integer pageLength = Integer.parseInt(pageLengthStr);
            iterations = total / pageLength;
            log.debug("Articles Found : " + total);

            ArrayList<Map> infoList = (ArrayList) result.get("records");
            for (Map info : infoList) {
                Article article = new Article();
                String title = (String) info.get("title");
                article.setTitle(title);
                article.setDoi((String) info.get("doi"));
                article.setJournal((String) info.get("publicationName"));

                ArrayList<Map> urlListMap = (ArrayList) info.get("url");
                article.setLink((String) urlListMap.get(0).get("value"));

                String dateStr = (String) info.get("publicationDate");
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    article.setPublishedDate(format.parse(dateStr));
                } catch (ParseException ex) {
                    log.error("Date error:" + dateStr + " for title: " + title);
                }
                ArrayList<Map> authorListMap = (ArrayList) info.get("creators");

                List<Author> authorList = new ArrayList();
                for (Map authorMap : authorListMap) {
                    String completeName = (String) authorMap.get("creator");
                    String[] name = completeName.split(", ");
                    Author author = new Author(name[1] + " " + name[0], null);
                    authorList.add(author);
                }
                article.setAuthorList(authorList);
                article.setAbstractText((String) info.get("abstract"));

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
        } while (iterations > 0);

    }

}

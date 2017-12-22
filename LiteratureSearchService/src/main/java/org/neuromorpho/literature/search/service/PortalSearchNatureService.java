/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.service;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.neuromorpho.literature.search.communication.ArticleResponse;
import org.neuromorpho.literature.search.model.article.Article;
import org.neuromorpho.literature.search.model.article.Author;
import org.neuromorpho.literature.search.model.article.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchNatureService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void searchForTitlesApi() {

        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        this.searchPortal = new Search(this.portal.getName(), this.keyWord);

        Integer total = 0;
        Integer startRecord = 1;
        do {//iterate over pages

            String uri = this.portal.getApiUrl()
                    + "queryType=searchTerms&query=" + this.keyWord
                    + " AND prism.publicationDate=" + yearFormat.format(this.startDate.getTime())
                    + "&httpAccept=application/json&startRecord=" + startRecord;
            log.debug("API retrieving from URI: " + uri);
            String data;
            try {
                data = Jsoup.connect(uri).ignoreContentType(true).execute().body();
                Gson gson = new Gson();
                Map result = gson.fromJson(data, Map.class);

                Map feed = (Map) result.get("feed");

                Double totalD = (Double) feed.get("opensearch:totalResults");
                total = totalD.intValue();
                Double pageLengthD = (Double) feed.get("opensearch:itemsPerPage");
                Integer pageLength = pageLengthD.intValue();

                startRecord = startRecord + pageLength;
                log.debug("Articles Found : " + total);

                ArrayList<Map> infoList = (ArrayList) feed.get("entry");
                if (infoList != null) {
                    for (Map info : infoList) {
                        Article article = new Article();

                        Map data1 = (Map) info.get("sru:recordData");
                        Map data2 = (Map) data1.get("pam:message");
                        Map data3 = (Map) data2.get("pam:article");
                        Map data4 = (Map) data3.get("xhtml:head");
                        String title1 = (String) data4.get("dc:title");
                        String title2 = title1.replace("<i>", "");
                        String title = title2.replace("</i>", "");
                        article.setTitle(title);
                        article.setDoi((String) data4.get("prism:doi"));
                        article.setJournal((String) data4.get("prism:publicationName"));
                        article.setLink((String) data4.get("link"));

                        String dateStr = (String) data4.get("prism:publicationDate");
                        try {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            article.setPublishedDate(format.parse(dateStr));
                        } catch (ParseException ex) {
                            log.error("Date error:" + dateStr + " for title: " + title);
                        }
                        ArrayList<String> authorListMap = (ArrayList) data4.get("dc:creator");

                        List<Author> authorList = new ArrayList();
                        if (authorListMap != null) {
                            for (String authorStr : authorListMap) {
                                Author author = new Author(authorStr, null);
                                authorList.add(author);
                            }
                        }
                        article.setAuthorList(authorList);
                        article.setAbstractText((String) data4.get("dc:description"));

                        //call pubmed to retrieve pubmedID
                        String pmid = pubMedConnection.findTitleFromPMID(title, "pubmed");
                        if (pmid == null) {
                            pmid = pubMedConnection.findTitleFromPMID(title, "pmc");
                        }
                        if (pmid != null) {
                            article.setPmid(pmid);
                        }
                        log.debug(article.toString());
                        if (!authorList.isEmpty()) {
                            // calling rest to save the article & updating the portal search values
                            ArticleResponse response = literatureConnection.saveArticle(article, Boolean.FALSE, this.collection);
                            log.debug(searchPortal.toString());

                            literatureConnection.saveSearchPortal(response.getId(), this.searchPortal);
                        }
                    }
                }
            } catch (IOException ex) {
                log.error("Exception loading url", ex);
            }
        } while (startRecord <= total);

    }
}

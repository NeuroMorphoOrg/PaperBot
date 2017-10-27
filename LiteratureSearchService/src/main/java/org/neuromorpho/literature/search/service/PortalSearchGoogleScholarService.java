/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.model.article.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchGoogleScholarService extends PortalSearch {

    private final Integer maxMin = 10;
    private final Integer minMin = 4;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void searchPage() {
        try {
            DateFormat yearFormat = new SimpleDateFormat("yyyy");
            List<String> queryParameterList = new ArrayList<>();
            queryParameterList.add("q=" + this.keyWord);
            queryParameterList.add("hl=en");
            queryParameterList.add("&as_sdt=0,47");
            queryParameterList.add("as_ylo=" + yearFormat.format(this.startDate.getTime()));

            String queyParamsStr = "";
            for (String queryParameter : queryParameterList) {
                queyParamsStr = queyParamsStr + "&" + queryParameter;
            }
            String urlFinal = this.portal.getUrl() + "?" + queyParamsStr;
            log.debug("Accessing portal url: " + urlFinal);
            this.searchDoc = Jsoup.connect(urlFinal)
                    .timeout(30 * 1000)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36").get();

        } catch (IOException ex) {
            log.error("Exception trying to load the url:" + this.portal.getUrl(), ex);
        }

    }

    @Override
    protected Elements findArticleList() {
        Elements articleList = this.searchDoc.select("div[class=gs_r]");
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {

        Element elem = articleData.select("h3[class=gs_rt] > a").first();
        String articleLink = elem.attr("href");
        this.article.setTitle(elem.text().trim());
        this.searchPortal.setLink(this.portal.getBase() + articleLink);
        log.debug("Article title: " + this.article.getTitle());
        return articleLink;
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        try {
            Element elem = articleData.select("div[class=gs_a]").first();
            String metadata = elem.text().split(" - ")[1];
            this.article.setJournal(metadata.split(",")[0]);
        } catch (Exception e) {
            log.error("Error filling journal for article: " + this.article.getTitle(), e);
        }
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
        try {
            List<Author> authorList = new ArrayList();
            Element elem = articleData.select("div[class=gs_a]").first();
            String metadata = elem.text().split(" - ")[0];
            String[] authorListStr = metadata.split(",");
            for (String authorStr : authorListStr) {
                String completeName = authorStr.replace("…", "").trim();
                Author author = new Author(completeName, null);
                authorList.add(author);

            }
            this.article.setAuthorList(authorList);
        } catch (Exception e) {
            log.error("Error filling authors for article: " + this.article.getTitle(), e);
        }
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        try {
            Element elem = articleData.select("div[class=gs_a]").first();
            String metadata = elem.text().split(" - ")[1];
            String[] dateArray = metadata.split(",");
            String date;
            if (dateArray.length == 1) {
                date = dateArray[0];
            } else {
                date = dateArray[1];
            }
            Date publishedDate = this.tryParseDate(date);
            this.article.setPublishedDate(publishedDate);
        } catch (Exception e) {
            log.error("Error filling date for article: " + this.article.getTitle(), e);
        }
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        //No DOI
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        Element elem = articleData.select("h3[class=gs_rt] > a").first();
        this.searchPortal.setLink(elem.attr("href"));
    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Elements linkList = this.searchDoc.select("div[id=gs_n] td[align=left] > a");
            if (linkList != null && !linkList.isEmpty()) {
                //simulate human behaviour sleep randome minutes between labor hours
                Random rand = new Random();
                // nextInt is normally exclusive of the top value,
                // so add 1 to make it inclusive
                Integer randomNum = rand.nextInt((maxMin - minMin) + 1) + minMin;
                log.debug("Random minutes to sleep: " + randomNum);
                log.debug("......................................");
                Thread.sleep(randomNum * 60 * 1000);
                String link = linkList.get(linkList.size() - 1).attr("href");
                log.debug("Loading next page: " + this.portal.getBase() + link);

                this.searchDoc = Jsoup.connect(this.portal.getBase() + link)
                        .timeout(30 * 1000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8").get();
                nextPage = Boolean.TRUE;

            }
        } catch (IOException ex) {
            log.error("Exception loading next page", ex);
        } catch (InterruptedException ex) {
            log.error("Exception with random sleep process ", ex);
        }
        return nextPage;
    }

    @Override
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        //think if from google can be done
    }

    @Override
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not needed if accessing through web and not API");
    }

}

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
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.neuromorpho.literature.search.model.article.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchWileyService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void searchPage() {
        try {
            DateFormat yearFormat = new SimpleDateFormat("yyyy");
            List<String> queryParameterList = new ArrayList<>();
            queryParameterList.add("searchRowCriteria[0].queryString=" + this.keyWord);
            queryParameterList.add("searchRowCriteria[0].fieldName=all-fields");
            queryParameterList.add("searchRowCriteria[0].booleanConnector=and");
            queryParameterList.add("dateRange=between");
            queryParameterList.add("startYear=" + yearFormat.format(this.startDate.getTime()));
            queryParameterList.add("endYear=" + yearFormat.format(this.endDate.getTime()));
            String queyParamsStr = "";
            for (String queryParameter : queryParameterList) {
                queyParamsStr = queyParamsStr + "&" + queryParameter;
            }
            String urlFinal = this.portal.getUrl() + "?" + queyParamsStr;
            log.debug("Accessing portal url: " + urlFinal);
            this.searchDoc = Jsoup.connect(urlFinal)
                    .timeout(30 * 1000)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .userAgent("Chrome").post();

        } catch (IOException ex) {
            log.error("Exception trying to load the url:" + this.portal.getUrl());
        }

    }

    @Override
    protected Elements findArticleList() {
        Elements articleList = this.searchDoc.select(".articles > li");
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {
        Element elem = articleData.select("div[class^=citation] a").first();
        String articleLink = elem.attr("href");
        this.article.setTitle(elem.text());
        return this.portal.getBase() + articleLink;
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        Element elem = articleData.select("div[class^=citation] h3").get(0);
        String journal = elem.text().substring(0, 1).toUpperCase()
                + elem.text().substring(1).toLowerCase();
        this.article.setJournal(journal);
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
//        try {
        List<Author> authorList = new ArrayList();
        List<Element> elemList = articlePage.select("div[class=article-header__authors-container] > ul > li");
        if (!elemList.isEmpty()) {
            for (Element elem : elemList) {
                String authorStr = elem.attr("data-author-name");

                List<Element> contactEmail = elem.select("a[class=article-header__authors-item-email]");
                String contactEmailStr = null;
                if (contactEmail.size() > 0) {
                    contactEmailStr = contactEmail.get(0).text();
                }
                Author author = new Author(authorStr, contactEmailStr);
                authorList.add(author);
            }

        } else {
            elemList = articlePage.select("div[id=articleMeta] > ol[id=authors] > li");
            for (Element elem : elemList) {
                String authorStr = elem.text().replaceAll("[^A-Za-z \\.]", "");
                Author author = new Author(authorStr, null);
                authorList.add(author);
            }
        }
        this.article.setAuthorList(authorList);

    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage
    ) {
        fillDate1(articlePage);
        if (article.getPublishedDate() == null) {
            fillDate2(articlePage);
        }

    }
    // The journal of Physiology

    private void fillDate1(Element articlePage) {
        Element elem = articlePage.select("time[id=first-published-date]").first();
        if (elem != null) {
            Date publishedDate = this.tryParseDate(elem.text());
            this.article.setPublishedDate(publishedDate);
        }
    }

    private void fillDate2(Element articlePage) {
        Element elem = articlePage.select("div[id=articleMeta] p[id=publishedOnlineDate]").first();
        String date = elem.text().split(": ")[1];
        Date publishedDate = this.tryParseDate(date);
        this.article.setPublishedDate(publishedDate);
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        fillDoi1(articlePage);
        if (article.getDoi() == null) {
            fillDoi2(articlePage);
        }
    }

    private void fillDoi1(Element articlePage) {
        Element elem = articlePage.select("div[id=articleMeta] > p[id=doi]").first();
        if (elem != null) {
            String doi = elem.text();
            doi = doi.replace("DOI: ", "");
            article.setDoi(doi);
        }
    }

    // The journal of Physiology
    private void fillDoi2(Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_doi]").first();
        article.setDoi(elem.attr("content"));
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        this.fillLink(articleData, articlePage);
    }

    private void fillLink(Element articleData, Element articlePage) {
        List<Element> elementList = articleData.select("ul[class=productMenu] > li > a");
        for (Element element : elementList) {
            if (element.text().startsWith("PDF") && this.searchPortal.getLink() != null) {
                this.searchPortal.setLink(this.portal.getBase() + element.attr("href"));
            }
        }

        List<Element> supplementaryLinkElementList = articlePage.select("section[aria-labelledby=supplementary-information] h3[class=text17] > a");
        log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
        for (Element supplementaryLinkElement : supplementaryLinkElementList) {
            String supplementaryLink = supplementaryLinkElement.attr("href");
            log.debug(supplementaryLink);
            this.searchPortal.setSupplementaryLink(supplementaryLink);
        }

    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Elements linkList = this.searchDoc.select("div[class=paginationFilter] > ol > li > a");
            if (linkList != null && !linkList.isEmpty()) {
                String link = linkList.get(linkList.size()-1).attr("href");
                this.searchDoc = Jsoup.connect(this.portal.getBase() + link)
                        .timeout(30 * 1000)
                        .userAgent("Chrome")
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8").get();
                nextPage = Boolean.TRUE;

            }
        } catch (IOException ex) {
            log.error("Exception loading next page", ex);
        }
        return nextPage;
    }

    @Override
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        Element link = articleData.select("div[class=access] span").first();
        if (!link.text().equals("You have full text access to this content")) {
            this.inaccessible = Boolean.TRUE;
        }
    }

    @Override
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not needed if accessing through web and not API");
    }

}

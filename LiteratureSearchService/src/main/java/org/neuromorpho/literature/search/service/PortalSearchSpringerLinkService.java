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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.model.article.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchSpringerLinkService extends PortalSearch {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void searchPage() {
        try {
            DateFormat yearFormat = new SimpleDateFormat("yyyy");

            List<String> queryParameterList = new ArrayList<>();
            queryParameterList.add("query=" + this.keyWord);
            queryParameterList.add("facet-start-year=" + yearFormat.format(this.startDate.getTime()));
            queryParameterList.add("facet-end-year=" + yearFormat.format(this.endDate.getTime()));
            queryParameterList.add("date-facet-mode=between");

            String queyParamsStr = "";
            for (String queryParameter : queryParameterList) {
                queyParamsStr = queyParamsStr + "&" + queryParameter;
            }
            String urlFinal = this.portal.getUrl() + "?" + queyParamsStr;
            log.debug("Accessing portal url: " + urlFinal);
            this.searchDoc = Jsoup.connect(urlFinal)
                    .timeout(30 * 1000)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .userAgent("Chrome").get();
        } catch (IOException ex) {
            log.error("Exception trying to load the url:" + this.portal.getUrl());
        }
    }

    @Override
    protected Elements findArticleList() {
        Elements articleList = this.searchDoc.select("ol[id=results-list] > li");
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {
        Element elem = articleData.select("h2 > a[class=title]").first();
        String articleLink = elem.attr("href");
        this.article.setTitle(elem.text());
        return this.portal.getBase() + articleLink;
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        fillJournal1(articlePage);
        if (this.article.getJournal() == null) {
            fillJournal2(articlePage);
        }
        if (this.article.getJournal() == null) {
            fillBook(articlePage);
        }
    }

    private void fillJournal1(Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_journal_title]").first();
        if (elem != null) {
            this.article.setJournal(elem.attr("content"));
        }
    }

    // For inaccessible articles
    private void fillJournal2(Element articlePage) {
        Element elem = articlePage.select("p[class=BookTitle] > a").first();
        if (elem != null) {
            this.article.setJournal(elem.text());
        }
    }

    private void fillBook(Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_inbook_title]").first();
        if (elem != null) {
            this.article.setJournal(elem.attr("content"));
        }
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
        List<Author> authorList = new ArrayList();
        Elements elemList = articlePage.select("div[class=authors__list] > ul > li");

        for (Element elem : elemList) {
            Element authorName = elem.select("span[class=authors__name]").first();
            Elements contactEmail = elem.select("span[class=author-information] a[class=gtm-email-author]");
            String contactEmailStr = null;
            if (contactEmail.size() > 0) {
                contactEmailStr = contactEmail.get(0).attr("href").split(":")[1];
            }
            Author author = new Author(authorName.text(), contactEmailStr);
            authorList.add(author);
        }
        this.article.setAuthorList(authorList);
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        this.fillPublishedDate1(articlePage);
        if (article.getPublishedDate() == null) {
            this.fillPublishedDate2(articlePage);
        }
        if (article.getPublishedDate() == null) {
            this.fillPublishedDate3(articlePage);
        }

    }

    private void fillPublishedDate1(Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_online_date]").first();
        if (elem != null) {
            Date publishedDate = this.tryParseDate(elem.attr("content"));
            this.article.setPublishedDate(publishedDate);
        }
    }

    private void fillPublishedDate2(Element articlePage) {
        Element elem = articlePage.select("span[class=version-date] > time").first();
        if (elem != null) {

            Date publishedDate = this.tryParseDate(elem.text());
            this.article.setPublishedDate(publishedDate);
        }
    }

    private void fillPublishedDate3(Element articlePage) {
        Element elem = articlePage.select("dd[class=article-dates__first-online] > time").first();
        if (elem != null) {
            Date publishedDate = this.tryParseDate(elem.attr("datetime"));
            this.article.setPublishedDate(publishedDate);
        }

    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        fillDoi1(articlePage);
        if (article.getDoi() == null) {
            fillDoi2(articlePage);
        }

    }

    private void fillDoi1(Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_doi]").first();
        if (elem != null) {
            article.setDoi(elem.attr("content"));
        }

    }

    private void fillDoi2(Element articlePage) {
        Element elem = articlePage.select("dd[@class=doi]").first();
        if (elem != null) {
            article.setDoi(elem.text());
        }
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        this.fillLink(articlePage);
        this.fillSupLinks(articlePage);
    }

    private void fillLink(Element articlePage) {
        Element element = articlePage.select("meta[name=citation_pdf_url]").first();
        this.searchPortal.setLink(element.attr("content"));
    }

    private void fillSupLinks(Element articlePage) {
        Elements supplementaryLinkElementList = articlePage.select("section[id=SupplementaryMaterial] a");
        log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
//        for (Element supplementaryLinkElement : supplementaryLinkElementList) {
//            String supplementaryLink = supplementaryLinkElement.attr("href");
//            log.debug(supplementaryLink);
//            this.searchPortal.setSupplementaryLink(supplementaryLink);
//        }
    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Elements linkList = this.searchDoc.select("div[class=functions-bar functions-bar-bottom] a[class=next]");
            if (linkList != null && !linkList.isEmpty()) {
                String link = linkList.get(linkList.size() - 1).attr("href");
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
        Element link = articlePage.select("form[id=getaccess-webshop]").first();
        if (link != null) {
            this.inaccessible = Boolean.TRUE;
        }
    }

    @Override
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not needed if accessing through web and not API");
    }

}

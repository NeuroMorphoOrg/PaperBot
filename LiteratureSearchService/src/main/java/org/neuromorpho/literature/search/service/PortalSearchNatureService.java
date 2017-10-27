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
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchNatureService extends PortalSearch {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void searchPage() {
        try {

            DateFormat yearFormat = new SimpleDateFormat("yyyy");
            List<String> queryParameterList = new ArrayList<>();
            queryParameterList.add("date_range=" + yearFormat.format(this.startDate.getTime()) + "-" + yearFormat.format(this.endDate.getTime()));
            queryParameterList.add("q=" + this.keyWord);

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
        Elements articleList = this.searchDoc.select("ol[class=clean-list] > li");
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {
        Element elem = articleData.select("div > h2[role=heading] > a").first();
        String articleLink = elem.attr("href");
        this.article.setTitle(elem.text());
        log.debug("Article title: " + this.article.getTitle());
        return articleLink;
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        Element elem = articleData.select("p >time").first();
        Date publishedDate = this.tryParseDate(elem.attr("datetime"));
        this.article.setPublishedDate(publishedDate);
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        Element elem = articleData.select("div > div > div[class=grid grid-7 mq640-grid-12 mt10] > a").first();
        this.article.setJournal(elem.text());

    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
        List<Author> authorList = new ArrayList();
        Elements authorElemList = articleData.select("div > ul > li[itemprop=creator]");
        for (Element authorElem : authorElemList) {
            String completeName = authorElem.text();
            completeName = completeName.replace(",", "");
            completeName = completeName.replace("&", "");
            completeName = completeName.trim();
            Author author = new Author(completeName, null);
            authorList.add(author);
        }
        this.article.setAuthorList(authorList);
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        Element doiElement = articlePage.select("meta[name=citation_doi]").first();
        article.setDoi(doiElement.attr("content").split(":")[1]);
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        this.fillLink1(articlePage);
        if (this.searchPortal.getLink() == null) {
            this.fillLink2(articlePage);
        }
        if (this.searchPortal.getLink() == null) {
            this.fillLink3(articlePage);
        }

    }

    private void fillLink1(Element articlePage) {
        Element pdfLink = articlePage.select("div[data-container-type=reading-companion] > div > ul > li > a").first();
        if (pdfLink != null) {
            this.searchPortal.setLink(pdfLink.attr("href"));

            Elements supplementaryLinkElementList = articlePage.select("section[aria-labelledby=supplementary-information] h3[class=text17] > a");
            log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
            for (Element supplementaryLinkElement : supplementaryLinkElementList) {
                String supplementaryLink = supplementaryLinkElement.attr("href");
                log.debug(supplementaryLink);
                this.searchPortal.setSupplementaryLink(supplementaryLink);
            }
        }

    }

    private void fillLink2(Element articlePage) {
        Element pdfLink = articlePage.select("li[class=download-option articlepdf] > a").first();
        if (pdfLink != null) {
            this.searchPortal.setLink(pdfLink.attr("href"));

            List<Element> supplementaryLinkElementList = articlePage.select("div[id=supplementary-information] > div[class=content] a");
            log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
            for (Element supplementaryLinkElement : supplementaryLinkElementList) {
                if (!supplementaryLinkElement.attr("href").endsWith(".html")) {
                    String supplementaryLink = supplementaryLinkElement.attr("href");
                    log.debug(supplementaryLink);
                    this.searchPortal.setSupplementaryLink(supplementaryLink);
                }
            }
//            supplementaryLinkElementList = articlePage.select("div[class=box supp-info] h2[class=pdf] >following-sibling::ol dt > a");
//            log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
//            for (Element supplementaryLinkElement : supplementaryLinkElementList) {
//                String supplementaryLink = supplementaryLinkElement.attr("href");
//                log.debug(supplementaryLink);
//                this.searchPortal.setSupplementaryLink(supplementaryLink);
//            }
        }

    }

    private void fillLink3(Element articlePage) {
        Element pdfLink = articlePage.select(".a[class=download-pdf]").first();

        if (pdfLink != null) {
            this.searchPortal.setLink(pdfLink.attr("href"));
        }
//        Elements supplementaryLinkElementList = articlePage.select("div[class=box supp-info] h2[class=pdf] > following-sibling::ol dt > a");
//        log.debug("Number of supplementary links: " + supplementaryLinkElementList.size());
//        for (Element supplementaryLinkElement : supplementaryLinkElementList) {
//            String supplementaryLink = supplementaryLinkElement.attr("href");
//            log.debug(supplementaryLink);
//            this.searchPortal.setSupplementaryLink(supplementaryLink);
//        }

    }

    @Override
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        fillIsAccessible1(articlePage);
        fillIsAccessible2(articlePage);
        fillIsAccessible3(articlePage);

    }

    private void fillIsAccessible1(Element articlePage) {
        Element element = articlePage.select("div[class=subscribe-prompt]").first();
        if (element != null) {
            this.inaccessible = Boolean.TRUE;
        }

    }

    private void fillIsAccessible2(Element articlePage) {
        Element element = articlePage.select("li[class=readcube-purchase]").first();
        if (element != null) {
            this.inaccessible = Boolean.TRUE;
        }

    }

    private void fillIsAccessible3(Element articlePage) {
        Element element = articlePage.select("ul[class=subscribe-box]").first();
        if (element != null) {
            this.inaccessible = Boolean.TRUE;
        }

    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Element link = this.searchDoc.select("li[data-page=next] > a").first();
            if (link != null) {
                String linkStr = link.attr("href");
                this.searchDoc = Jsoup.connect(this.portal.getBase() + linkStr)
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
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not needed if accessing through web and not API");
    }

}

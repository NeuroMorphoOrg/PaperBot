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
    protected void searchPage() throws IOException {
        String urlFinal = null;
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
        urlFinal = this.portal.getUrl() + "?" + queyParamsStr;
        log.debug("Accessing portal url: " + urlFinal);
        this.searchDoc = Jsoup.connect(urlFinal)
                .timeout(30 * 1000)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0.2 Safari/604.4.7").post();

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
        Element elem = articlePage.select("meta[name=citation_journal_title]").first();
        if (elem == null) {
            elem = articlePage.select("meta[name=citation_book_title]").first();
        }
        String journal = elem.attr("content");
        this.article.setJournal(journal);
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {

        List<Author> authorList = new ArrayList();
        Elements elemList = articlePage.select("meta[name=citation_author]");
        for (Element elem : elemList) {
            String[] authorStr = elem.attr("content").split(", ");
            Author author = new Author(authorStr[1] + " " + authorStr[0], null);
            authorList.add(author);
        }
        this.article.setAuthorList(authorList);

    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        try {
            Element elem = articlePage.select("meta[name=citation_online_date]").first();
            String date = elem.attr("content");
            Date publishedDate = this.tryParseDate(date);
            this.article.setPublishedDate(publishedDate);
        } catch (Exception ex) {
            log.error("Exception: error reading date");
        }
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_doi]").first();
        String doi = elem.attr("content");
        article.setDoi(doi);
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        Element elem = articlePage.select("meta[name=citation_fulltext_html_url]").first();
        if (elem == null){
            elem = articlePage.select("meta[name=citation_abstract_html_url]").first();
        }
        String link = elem.attr("content");
        this.article.setLink(link);
    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Element selected = this.searchDoc.select("div[class=paginationFilter] > ol > li[class=selected]").first();
            if (selected == null) {
                return nextPage;
            }
            Element next = selected.nextElementSibling();
            if (next == null) {
                return nextPage;
            }
            Element linkEl = next.selectFirst("a");
            String link = linkEl.attr("href");
            log.debug("Accessing portal url next page: " + this.portal.getBase() + link);

            this.searchDoc = Jsoup.connect(this.portal.getBase() + link)
                    .timeout(30 * 1000)
                    .userAgent("Chrome")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8").get();
            nextPage = Boolean.TRUE;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

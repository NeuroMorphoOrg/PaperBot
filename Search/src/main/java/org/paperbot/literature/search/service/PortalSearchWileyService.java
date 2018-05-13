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
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.paperbot.literature.search.model.article.Author;
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
        DateFormat monthFormat = new SimpleDateFormat("MM");
        List<String> queryParameterList = new ArrayList<>();
        queryParameterList.add("text1=" + this.keyWord.replace(" ", "+"));
        queryParameterList.add("field1=AllField");
        queryParameterList.add("AfterMonth=" + monthFormat.format(this.startDate.getTime()));
        queryParameterList.add("AfterYear=" + yearFormat.format(this.startDate.getTime()));
        String queyParamsStr = "";
        for (String queryParameter : queryParameterList) {
            queyParamsStr = queyParamsStr + "&" + queryParameter;
        }
        urlFinal = this.portal.getUrl() + "?" + queyParamsStr;
        log.debug("Accessing portal url: " + urlFinal);
        this.searchDoc = Jsoup.connect(urlFinal)
                .timeout(60 * 1000)
                .followRedirects(true)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36").get();

    }

    @Override
    protected Elements findArticleList() {
        Elements articleList = this.searchDoc.select("div[class=item__body]");
        log.debug("Page with #articles: " + articleList.size());
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {
        Element elem = articleData.select("span[class=hlFld-Title] > a").first();
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
            Author author = new Author(elem.attr("content"), null);
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
        if (elem == null) {
            elem = articlePage.select("meta[name=citation_abstract_html_url]").first();
        }
        String link = elem.attr("content");
        this.article.setLink(link);
    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Element selected = this.searchDoc.select("div[class=pagination] a[title=Next page]").first();
            if (selected == null) {
                return nextPage;
            }
            String link = selected.attr("href");
            log.debug("Accessing portal url next page: " + link);

            this.searchDoc = Jsoup.connect(link)
                    .timeout(30 * 1000)
                    .followRedirects(true)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36").get();
            nextPage = Boolean.TRUE;

        } catch (IOException ex) {
            log.error("Exception loading next page", ex);
        }
        return nextPage;
    }

    @Override
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

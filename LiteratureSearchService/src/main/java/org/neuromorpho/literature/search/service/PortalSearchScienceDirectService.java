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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neuromorpho.literature.search.model.article.Author;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalSearchScienceDirectService extends PortalSearch {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void searchPage() {
        try {
            String key = this.keyWord;
            int count = StringUtils.countMatches(this.keyWord, "\"");
            for (int j = 0; j < count; j = j + 2) {
                key = key.replaceFirst("\"", "\\{");
                key = key.replaceFirst("\"", "\\}");
            }
            if (this.keyWord.contains("_")) {
                key = "{" + this.keyWord + "}";
            }
            DateFormat yearFormat = new SimpleDateFormat("yyyy");
            List<String> queryParameterList = new ArrayList<>();
            queryParameterList.add("_ob=MiamiSearchURL");
            queryParameterList.add("_method=submitForm");
            queryParameterList.add("_acct=C000228598");
            queryParameterList.add("_temp=all_search.tmpl");
            queryParameterList.add("md5=bcb6a8104fc3e2467a87cd5c88f8d365");
            queryParameterList.add("test_alid=");
            queryParameterList.add("SearchText=" + key);
            queryParameterList.add("keywordOpt=ALL");
            queryParameterList.add("addTerm=0");
            queryParameterList.add("addSearchText=");
            queryParameterList.add("addkeywordOpt=ALL");
            queryParameterList.add("source=srcJrl");
            queryParameterList.add("source=srcBk");
            queryParameterList.add("srcSel=1");
            queryParameterList.add("DateOpt=0");
            queryParameterList.add("fromDate=" + yearFormat.format(this.startDate.getTime()));
            queryParameterList.add("toDate=Present");
            queryParameterList.add("RegularSearch=Search");

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
//        log.debug("Loading url " + this.portal.getUrl());
//        this.driverList.get(this.portal.getUrl());
//        this.driverList.select(".//div[@class='searchBlk']//li[@class='selected greyBg ']/a")
//        ).click();
//        Elements textList = this.driverList.selects(".//div[@class='searchBlk']//div[@class='contentMain']//div[@class='searchFormBg']/div/input[@class='inputBox searchBox']")
//        );
//       

    }

    @Override
    protected Elements findArticleList() {
        Elements articleList = this.searchDoc.select("ol[class=articleList results] > li > ul");
        return articleList;
    }

    @Override
    protected String fillTitle(Element articleData) {
        Element elem = articleData.select("li[class^=title] > h2 > a").first();
        this.article.setTitle(elem.text());
        log.debug("Article title: " + this.article.getTitle());

        String articleLink = elem.attr("href");
        this.article.setTitle(elem.text());
        return articleLink;
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        Element elem = articleData.select("li[class^=source]").first();
        if (elem != null) {
            String[] dateStrList = elem.text().split(",");
            String dateStr;
            if (dateStrList.length > 1) {
                dateStr = dateStrList[dateStrList.length - 2].trim();

            } else {
                dateStr = dateStrList[0].replace("Available online ", "");
            }
            Date date = tryParseDate(dateStr);
            this.article.setPublishedDate(date);
        }
    }

    @Override
    /*
    * DOI is loaded using javascript
     */
    protected void fillDoi(Element articleData, Element articlePage) {
        Element elem = articlePage.select("input[name=doi]").first();
        if (elem != null) {
            article.setDoi(elem.attr("value"));
        } else {
            Elements elements = articlePage.select("script");
            //Extract DOI from script javascript
            elem = elements.get(14);
            String doi = this.extractText(elem.toString(), "SDM.doi = '(.*?)';");
            article.setDoi(doi);
        }
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        Element elem = articleData.select("li[class^=source]").first();
        if (elem != null) {
            String[] elemStrList = elem.text().split(",");
            this.article.setJournal(elemStrList[0]);
        }
    }

    @Override
    protected Boolean loadNextPage() {
        Boolean nextPage = Boolean.FALSE;
        try {
            Elements linkList = this.searchDoc.select("li[class=pagination-link next-link] > a");
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
    protected void fillAuthorList(Element articleData, Element articlePage) {

        List<Author> authorList = new ArrayList();
        Elements elemList = articlePage.select("ul[class^=authorGroup] > li");

        for (Element elem : elemList) {

            Element authorName = elem.select("a[class^=authorName]").first();

            Elements contactEmail = elem.select("a[class=auth_mail]");
            String contactEmailStr = null;
            if (contactEmail.size() > 0) {
                contactEmailStr = contactEmail.get(0).attr("href").split(":")[1];
            }
            Author author = new Author(authorName.text(), contactEmailStr);
            authorList.add(author);
        }
        if (authorList.isEmpty()) {
            String elem = articleData.select("li[class^=authorTxt]").first().text();
            String[] authorListStr = elem.split(",");
            for (String authotStr : authorListStr) {
                Author author = new Author(authotStr.trim(), null);
                authorList.add(author);
            }
        }

        this.article.setAuthorList(authorList);

    }

    @Override
    protected void fillIsAccessible(Element articleData, Element articlePage) {
        Element elem = articlePage.select("a[id=pdfLink]").first();
        if (elem != null && elem.text().contains("Purchase")) {
            this.inaccessible = Boolean.TRUE;
        }
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {

        Element elem = articlePage.select("a[id=pdfLink]").first();
        if (elem != null) {
            this.searchPortal.setLink(elem.attr("href") + elem.attr("querystr"));
            Elements externalLinkList = articlePage.select("dd[class=ecomponent] > a");
            log.debug("Number of supplementary links: " + externalLinkList.size());
            for (Element externalLink : externalLinkList) {
                log.debug(externalLink.attr("outerHTML"));
                this.searchPortal.setSupplementaryLink(externalLink.attr("href"));
            }
        }
    }

    @Override
    protected void searchForTitlesApi() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String extractText(String str, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}

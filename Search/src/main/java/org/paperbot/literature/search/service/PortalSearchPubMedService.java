/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.search.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.paperbot.literature.search.model.article.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class PortalSearchPubMedService extends PortalSearch {

    private final static Logger log = LoggerFactory.getLogger(PortalSearchPubMedService.class);

    @Override
    public void searchForTitlesApi() throws InterruptedException {

        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        RestTemplate restTemplate = new RestTemplate();
        String uri = this.portal.getApiUrl() + "/esearch.fcgi?"
                + "db=" + this.portal.getDb()
                + "&retmode=json"
                + "&retmax=5000"
                + "&term=" + this.keyWord
                + " AND " + yearFormat.format(this.startDate.getTime()) + ":"
                + yearFormat.format(this.endDate.getTime()) + "[pdat]";
        log.debug("PubMed retrieving from URI: " + uri);

        Map<String, Object> pmidMap = restTemplate.getForObject(uri, Map.class);

        Map result = (HashMap) pmidMap.get("esearchresult");
        ArrayList<String> uidList = (ArrayList) result.get("idlist");
        log.debug("Articles Found : " + uidList.size());

        for (String uid : uidList) {
            try {
                this.inaccessible = Boolean.FALSE;
                article = new Article();
                if (this.portal.getDb().equals("pmc")){
                    uid = "PMC" + uid;
                }
                log.debug("PMID: " + uid);
                this.saveArticle(uid);
                
            } catch (HttpServerErrorException ex) {
                log.error("Exception: ", ex);
            }

        }
    }

    @Override
    protected Elements findArticleList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void searchPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Boolean loadNextPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String fillTitle(Element articleData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillPublishedDate(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillJournal(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillAuthorList(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillDoi(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fillLinks(Element articleData, Element articlePage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void searchForTitles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

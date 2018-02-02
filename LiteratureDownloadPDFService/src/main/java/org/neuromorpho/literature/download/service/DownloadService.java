/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.download.service;

import org.neuromorpho.literature.download.communication.Article;
import org.neuromorpho.literature.download.communication.ArticlePage;
import org.neuromorpho.literature.download.communication.CrosRefCommunication;
import org.neuromorpho.literature.download.communication.LiteratureCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {

    private final static Logger log = LoggerFactory.getLogger(DownloadService.class);
    @Autowired
    private LiteratureCommunication literatureCommunication;
    @Autowired
    private CrosRefCommunication crosRefCommunication;

    public void downloadPDFs() {

        Integer page = 0;
        ArticlePage articlePage;
        do {
            articlePage = literatureCommunication.getArticleList(page, "To evaluate");

            for (Article article : articlePage.getContent()) {
                try{
                String downloadedPath = crosRefCommunication.downloadPDF(article.getDoi(), article.getId());
                if (downloadedPath != null) {
                    literatureCommunication.updateArticlePdf(article.getId(), downloadedPath);
                }
                } catch (Exception ex){
                    log.error("CrossRefError: " + article.getDoi(), ex);
                } 
            }
            page++;

        } while (!articlePage.getLast());

    }

}

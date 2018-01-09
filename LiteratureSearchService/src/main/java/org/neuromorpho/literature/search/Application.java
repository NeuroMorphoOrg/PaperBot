/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search;

import java.util.List;
import org.neuromorpho.literature.search.communication.PortalConnection;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.neuromorpho.literature.search.service.IPortalSearch;
import org.neuromorpho.literature.search.service.PortalSearchFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {

    @Autowired
    private PortalSearchFactory portalSearchFactory;
   
    @Autowired
    private PortalConnection portalConnection;
   

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        List<Portal> portalList = portalConnection.findActivePortals();
        List<KeyWord> keyWordList = portalConnection.findAllKeyWords();

        for (Portal portal : portalList) {
            IPortalSearch portalSearch = portalSearchFactory.launchPortalSearch(portal.getName());

            for (KeyWord word : keyWordList) {
                portalSearch.findArticleList(word, portal);
            }

        }

    }

}

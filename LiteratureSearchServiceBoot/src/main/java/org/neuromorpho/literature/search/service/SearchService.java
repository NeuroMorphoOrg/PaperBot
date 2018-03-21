package org.neuromorpho.literature.search.service;

import java.util.List;
import org.neuromorpho.literature.search.communication.PortalConnection;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PortalSearchFactory portalSearchFactory;

    @Autowired
    private PortalConnection portalConnection;

    public void launchSearch() throws Exception {
        List<Portal> portalList = portalConnection.findActivePortals();
        List<KeyWord> keyWordList = portalConnection.findAllKeyWords();

        for (Portal portal : portalList) {
            IPortalSearch portalSearch = portalSearchFactory.launchPortalSearch(portal.getName());

            for (KeyWord keyWord : keyWordList) {
                List<KeyWord> wordList = keyWord.extractORs();
                for (KeyWord word : wordList) {
                    portalSearch.findArticleList(word, portal);
                }
            }

        }
    }

}

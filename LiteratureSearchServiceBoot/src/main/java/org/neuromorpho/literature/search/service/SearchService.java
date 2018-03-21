package org.neuromorpho.literature.search.service;

import java.util.List;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Log;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.neuromorpho.literature.search.repository.portal.KeyWordRepository;
import org.neuromorpho.literature.search.repository.portal.LogRepository;
import org.neuromorpho.literature.search.repository.portal.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    private PortalSearchFactory portalSearchFactory;

    @Autowired
    private PortalRepository portalRepository;
    @Autowired
    private KeyWordRepository keyWordRepository;
    @Autowired
    private LogRepository logRepository;

    public void launchSearch() throws Exception {
        List<Portal> portalList = portalRepository.findByActive(Boolean.TRUE);
        List<KeyWord> keyWordList = keyWordRepository.findAll();
        Log portalLog = new Log();
        for (Portal portal : portalList) {

            portalLog.statLogging();
            logRepository.save(portalLog);
            IPortalSearch portalSearch = portalSearchFactory.launchPortalSearch(portal.getName());

            for (KeyWord keyWord : keyWordList) {
                List<KeyWord> wordList = keyWord.extractORs();
                for (KeyWord word : wordList) {
                    portalSearch.findArticleList(word, portal);
                }
            }

        }
        portalLog.endLogging();
        logRepository.save(portalLog);
    }

}

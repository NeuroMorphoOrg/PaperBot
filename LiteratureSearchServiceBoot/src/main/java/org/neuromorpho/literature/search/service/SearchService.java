package org.neuromorpho.literature.search.service;

import java.util.List;
import java.util.Set;
import org.jsoup.HttpStatusException;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Log;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.neuromorpho.literature.search.repository.portal.KeyWordRepository;
import org.neuromorpho.literature.search.repository.portal.LogRepository;
import org.neuromorpho.literature.search.repository.portal.PortalRepository;
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
    private PortalRepository portalRepository;
    @Autowired
    private KeyWordRepository keyWordRepository;
    @Autowired
    private LogRepository logRepository;

    public void launchSearch() throws Exception {
        Log portalLog = new Log();
        portalLog.setStartDate();
        portalLog.setThreadId(Thread.currentThread().getId());
        log.debug("Thread id: " + Thread.currentThread().getId());
        logRepository.save(portalLog);
        portalLog.setCause("Search ends");
        try {
            List<Portal> portalList = portalRepository.findByActive(Boolean.TRUE);
            List<KeyWord> keyWordList = keyWordRepository.findAll();
            for (Portal portal : portalList) {
                try {
                    IPortalSearch portalSearch = portalSearchFactory.launchPortalSearch(portal.getName());
                    for (KeyWord keyWord : keyWordList) {
                        List<KeyWord> wordList = keyWord.extractORs();
                        for (KeyWord word : wordList) {
                            portalSearch.findArticleList(word, portal);
                        }
                    }
                } catch (HttpStatusException ex) {
                    portalLog.setCause("HTTP Connection Error for portal " + portal.getName());
                }
                portalLog.setCause("Finished");
            }
        } catch (InterruptedException ex) { //Interrupted exception is not able to write in mongo
            log.warn("The user has interrupted the search");
        } catch (Exception ex) {
            log.error("Unknown error", ex);
            portalLog.setCause("Unknown Error");
        } finally {
            portalLog.setStopDate();
            logRepository.save(portalLog);
        }
    }

    public void stopSearch() throws Exception {

        Log portalLog = logRepository.findFirstByOrderByStartDesc();

        Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();

        for (Thread thread : setOfThread) {
            if (thread.getId() == portalLog.getThreadId()) {
                portalLog.setStopDate();
                portalLog.setCause("Interrupted by user");
                logRepository.save(portalLog);
                thread.interrupt();
            }

        }
    }
}

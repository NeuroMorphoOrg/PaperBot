package org.paperbot.literature.search.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.paperbot.literature.search.service.SearchService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping()
public class SearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SearchService searchService;

    @CrossOrigin
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public void launchSearch() throws Exception {
        log.debug("Launching full search");
        searchService.launchSearch();
    }

    @CrossOrigin
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public void stopSearch() throws Exception {
        log.debug("Stopping thead");
        searchService.stopSearch();
    }

}

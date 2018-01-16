package org.neuromorpho.literature.crosref.controller;

import org.neuromorpho.literature.crosref.model.Article;
import org.neuromorpho.literature.crosref.service.CrosRefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/crosref")
public class CrosRefController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrosRefService crosRefService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "CrosRef up & running!";
    }

    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Article retrieveCrosRefArticleData(
            @RequestParam(required = true) String doi) throws Exception {
        log.debug("Calling pubmed with pmid: " + doi);
        Article article = crosRefService.retrieveCrosRefArticleData(doi);
        log.debug("Data from crosef: " + article.toString());
        return article;

    }

    @CrossOrigin
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String downloadPDFFromDOI(
            @RequestParam(required = true) String doi,
            @RequestParam(required = true) String id) throws Exception {
        log.debug("Calling pubmed with title: " + doi);
        String folder = crosRefService.downloadPDFFromDOI(doi, id);
        log.debug("Folder where th PDF is downloaded: " + doi);
        return folder;

    }

}

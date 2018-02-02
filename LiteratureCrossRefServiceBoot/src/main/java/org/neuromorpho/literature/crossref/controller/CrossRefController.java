package org.neuromorpho.literature.crossref.controller;

import org.neuromorpho.literature.crossref.model.Article;
import org.neuromorpho.literature.crossref.service.CrossRefService;
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
public class CrossRefController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrossRefService crossRefService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "CrosRef up & running!";
    }

    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Article retrieveCrossRefArticleData(
            @RequestParam(required = true) String doi) throws Exception {
        log.debug("Calling crosref with doi: " + doi);
        Article article = crossRefService.retrieveArticleData(doi);
        log.debug("Data from crosef: " + article.toString());
        return article;

    }

    @CrossOrigin
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String downloadPDFFromDOI(
            @RequestParam(required = true) String doi,
            @RequestParam(required = true) String id) throws Exception {
        log.debug("Calling crosRef with DOI: " + doi);
        String folder = crossRefService.downloadPDFFromDOI(doi, id);
        log.debug("Folder where the PDF is downloaded: " + folder);
        return folder;

    }

}

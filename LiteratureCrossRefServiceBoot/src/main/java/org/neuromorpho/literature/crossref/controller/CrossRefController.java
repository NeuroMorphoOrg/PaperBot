package org.neuromorpho.literature.crossref.controller;

import com.mongodb.gridfs.GridFSDBFile;
import org.neuromorpho.literature.crossref.model.Article;
import org.neuromorpho.literature.crossref.service.CrossRefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/crossref")
public class CrossRefController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrossRefService crossRefService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "CrossRef up & running!";
    }

    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Article retrieveCrossRefArticleData(
            @RequestParam(required = true) String doi) throws Exception {
        log.debug("Calling crossref with doi: " + doi);
        Article article = crossRefService.retrieveArticleData(doi);
        log.debug("Data from crossef: " + article.toString());
        return article;

    }

    @CrossOrigin
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadPDFFromDOI(
            @RequestParam(required = true) String doi,
            @RequestParam(required = true) String id) throws Exception {
        log.debug("Calling crossRef with DOI: " + doi);
        crossRefService.downloadPDFFromDOI(doi, id);
    }

    @CrossOrigin
    @RequestMapping(value = "/load/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> findPDF(
            @PathVariable String id) {
        log.debug("Reading PDF id: " + id);
        GridFSDBFile gFile = crossRefService.findPDF(id);
        InputStreamResource inputStreamResource = new InputStreamResource(gFile.getInputStream());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(gFile.getLength());
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
        return new ResponseEntity<> (inputStreamResource,
                                   responseHeaders,
                                   HttpStatus.OK);
    }

}

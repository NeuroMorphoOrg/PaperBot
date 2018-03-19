package org.neuromorpho.literature.portal.controller;

import java.util.List;
import org.neuromorpho.literature.portal.model.KeyWord;
import org.neuromorpho.literature.portal.service.KeyWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/keywords")
public class KeyWordController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private KeyWordService keyWordService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public List<KeyWord> getKeyWordList() {
        return keyWordService.findAll();
       
    }
    
     @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT)
    public void updateKeyWordList(@RequestBody List<KeyWord> keyWordList) {
        keyWordService.updateKeyWordList(keyWordList);
       
    }

}

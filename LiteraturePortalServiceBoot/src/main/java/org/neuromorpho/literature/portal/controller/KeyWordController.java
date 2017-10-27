package org.neuromorpho.literature.portal.controller;

import java.util.ArrayList;
import java.util.List;
import org.neuromorpho.literature.portal.model.KeyWord;
import org.neuromorpho.literature.portal.service.KeyWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/keywords")
public class KeyWordController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final KeyWordDtoAssembler keyWordDtoAssembler = new KeyWordDtoAssembler();

    @Autowired
    private KeyWordService keyWordService;

    @RequestMapping(method = RequestMethod.GET)
    public List<KeyWordDto> getKeyWordList() {
        List<KeyWord> keyWordList = keyWordService.findAll();
        List<KeyWordDto> keyWordDtoList = new ArrayList();
        for (KeyWord keyWord : keyWordList) {
            keyWordDtoList.add(keyWordDtoAssembler.createKeyWordDto(keyWord));
        }
        return keyWordDtoList;
    }

}

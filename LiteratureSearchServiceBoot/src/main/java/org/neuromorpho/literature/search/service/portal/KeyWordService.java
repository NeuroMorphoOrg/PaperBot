/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.search.service.portal;

import java.util.List;
import org.bson.types.ObjectId;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.repository.portal.KeyWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyWordService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KeyWordRepository keyWordRepository;

    public List<KeyWord> findAll() {
        log.debug("Retrieving all keywords from DB");
        return keyWordRepository.findAll();
    }

    public void updateKeyWordList(List<KeyWord> keyWordList) {
        log.debug("Updating keywords into DB");
        keyWordRepository.save(keyWordList);
    }
    
    public void deleteKeyWordList(List<String> ids) {
        log.debug("Removing keywords from DB");
        for (String id: ids){
            keyWordRepository.delete(new ObjectId(id));
        }
    }
}

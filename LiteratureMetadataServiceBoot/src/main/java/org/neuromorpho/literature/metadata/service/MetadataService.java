/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.metadata.service;

import java.util.List;
import org.bson.types.ObjectId;
import org.neuromorpho.literature.metadata.model.MetadataFirstStage;
import org.neuromorpho.literature.metadata.model.MetadataValues;
import org.neuromorpho.literature.metadata.repository.MetadataRepository;
import org.neuromorpho.literature.metadata.repository.MetadataValuesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final String collection = "metadata";

    @Autowired
    private MetadataRepository metadataRepository;
    
    @Autowired
    private MetadataValuesRepository metadataValuesRepository;
         
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getDistinctByKey(String key) {
        key = "attributes." + key;
        log.debug("Geting metadata disctinct values for field" + key);
        List<String> valueList = mongoTemplate.getCollection(collection).distinct(key);
        return valueList;
    }
    
    public List<MetadataValues> getByKey(String key) {
        log.debug("Geting metadata values for field" + key);
        return metadataValuesRepository.findByType(key);
    }
    
    public MetadataFirstStage find(ObjectId id) {
        log.debug("Metadata for article id: " + id);
        return metadataRepository.findOne(id);
    }
     
    public void save(MetadataFirstStage metadata) {
        log.debug("Saving or updating metadata: " + metadata.toString());
        metadataRepository.save(metadata);
    }

}

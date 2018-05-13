/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.metadata.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.paperbot.literature.metadata.model.MetadataValues;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataValuesRepository extends MongoRepository<MetadataValues, ObjectId> {

    public List<MetadataValues> findByType(String type);

}

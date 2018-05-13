/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.search.repository.portal;


import org.bson.types.ObjectId;
import org.paperbot.literature.search.model.portal.KeyWord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyWordRepository extends MongoRepository<KeyWord, ObjectId> {
 
}

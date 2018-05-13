/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paperbot.literature.search.repository.portal;

import java.util.List;
import org.bson.types.ObjectId;
import org.paperbot.literature.search.model.portal.Portal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends MongoRepository<Portal, ObjectId> {

    List<Portal> findByActive(Boolean active);
}

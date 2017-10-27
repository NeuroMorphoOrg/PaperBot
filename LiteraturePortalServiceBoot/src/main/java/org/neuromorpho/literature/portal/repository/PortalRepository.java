/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.neuromorpho.literature.portal.model.Portal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends MongoRepository<Portal, ObjectId> {

    List<Portal> findByActive(Boolean active);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.repository;

import java.util.Date;
import org.neuromorpho.literature.portal.model.Log;
import org.neuromorpho.literature.portal.model.PortalLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class PortalLogExtendedRepositoryImpl implements PortalLogExtendedRepository {

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void updateLog(String portalName, Date launchedDate, Integer total) {

        Log launch = new Log(launchedDate, total);
        Query query = new Query(Criteria.where("name").is(portalName));
        if (mongoOperations.findOne(query, PortalLog.class) == null) {
            PortalLog portal = new PortalLog(portalName);
            mongoOperations.save(portal);
        }
        mongoOperations.updateFirst(Query.query(Criteria.where("name").is(portalName)),
                new Update().push("launch", launch), PortalLog.class);
    }

}

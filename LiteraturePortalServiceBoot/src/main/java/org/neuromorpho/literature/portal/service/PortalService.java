/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.service;

import java.util.List;
import org.neuromorpho.literature.portal.model.Portal;
import org.neuromorpho.literature.portal.repository.PortalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortalService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PortalRepository portalRepository;

    public List<Portal> findActives() {
        log.debug("Retrieving the actives portals from DB");
        return portalRepository.findByActive(Boolean.TRUE);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.controller;

import org.neuromorpho.literature.portal.model.Portal;


public class PortalDtoAssembler {
    
    public PortalDto createPortalDto(Portal portal) {
        PortalDto portalDto = new PortalDto();
        portalDto.setName(portal.getName());
        portalDto.setSearchPeriod(portal.getSearchPeriod());
        portalDto.setUrl(portal.getUrl());
        portalDto.setApiUrl(portal.getApiUrl());
        portalDto.setApiUrl2(portal.getApiUrl2());
        portalDto.setDb(portal.getDb());
        portalDto.setBase(portal.getBase());

        return portalDto;
    }
    
}

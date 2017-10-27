/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller;

import org.neuromorpho.literature.model.article.SearchPortal;

public class SearchPortalDtoAssembler {

    public SearchPortalDtoAssembler() {
    }

    public SearchPortalDto createSearchPortalDto(SearchPortal searchPortal) {
        if (searchPortal != null) {
            SearchPortalDto searchPortalDto = new SearchPortalDto();
            searchPortalDto.setLink(searchPortal.getLink());
            searchPortalDto.setSource(searchPortal.getSource());
            searchPortalDto.setPortalList(searchPortal.getPortalList());
            return searchPortalDto;
        }
        return null;
    }

    public SearchPortal createSearchPortal(SearchPortalDto searchPortalDto) {
        SearchPortal searchPortal = new SearchPortal();
        if (searchPortalDto != null) {
            searchPortal.setSource(searchPortalDto.getSource());
        }
        return searchPortal;
    }

}

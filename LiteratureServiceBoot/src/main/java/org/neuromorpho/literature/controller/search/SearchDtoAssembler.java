/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller.search;

import org.neuromorpho.literature.model.article.SearchPortal;

public class SearchDtoAssembler {

 public SearchPortal createSearch(SearchDto searchDto) {
        SearchPortal searchPortal = new SearchPortal(
                searchDto.getSource(), searchDto.getKeyWord(), searchDto.getLink());
        return searchPortal;
    }
 
}

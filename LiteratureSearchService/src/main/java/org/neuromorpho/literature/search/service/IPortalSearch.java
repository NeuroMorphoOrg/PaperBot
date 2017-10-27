package org.neuromorpho.literature.search.service;

import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;




public interface IPortalSearch {
    
    public Integer findArticleList(KeyWord keyWord, Portal portal) throws Exception;
    
}

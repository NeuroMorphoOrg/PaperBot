package org.paperbot.literature.search.service;

import org.paperbot.literature.search.model.portal.KeyWord;
import org.paperbot.literature.search.model.portal.Portal;




public interface IPortalSearch {
    
    public void findArticleList(KeyWord keyWord, Portal portal) throws Exception;
    
}

package org.neuromorpho.literature.search.communication;


import java.util.Arrays;
import java.util.List;
import org.neuromorpho.literature.search.model.portal.KeyWord;
import org.neuromorpho.literature.search.model.portal.Portal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PortalConnection {

    @Value("${uriPortalService}")
    private String uri;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
     
    public List<Portal> findActivePortals() {
        String url = uri + "/portals";
        log.debug("Creating rest connection for URI: " + url);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Portal[]> responseEntity = 
                restTemplate.getForEntity(url, Portal[].class);
        return Arrays.asList(responseEntity.getBody());
    }

     public List<KeyWord> findAllKeyWords() {
        String url = uri + "/keywords";
        log.debug("Creating rest connection for URI: " + url);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KeyWord[]> responseEntity = 
                restTemplate.getForEntity(url, KeyWord[].class);
        return Arrays.asList(responseEntity.getBody());
    }

}

package org.neuromorpho.literature.crossref.communication;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CrossRefConnection {

    @Value("${uri}")
    private String uri;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Map findMetadataFromDOI(String doi) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = uri
                + "works/" + doi;
        log.debug("Accesing crosef using url: " + url);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            Map<String, Object> articleMap = response.getBody();
            return (HashMap) articleMap.get("message");
        } else {
            return null;
        }
    }

}

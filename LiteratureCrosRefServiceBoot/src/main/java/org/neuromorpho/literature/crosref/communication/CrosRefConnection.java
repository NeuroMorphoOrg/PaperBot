package org.neuromorpho.literature.crosref.communication;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CrosRefConnection {

    @Value("${uri}")
    private String uri;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Map findMetadataFromDOI(String doi) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = uri
                + "works/" + doi;
        log.debug("Accesing crosef using url: " + url);
        Map<String, Object> articleMap = restTemplate.getForObject(url, Map.class);
        Map message = (HashMap) articleMap.get("message");
        if (message == null) {
            throw new Exception("Unknown doi not found in CrosRef: " + doi);
        }
        return message;
    }

}

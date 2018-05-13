package org.paperbot.literature.search.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CrossRefConnection {

    @Value("${uriCrosRef}")
    private String uri;
    @Value("${download}")
    private String download;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Boolean downloadPDF(String doi, String id) {
        try {
            String url = uri + "?doi=" + doi + "&id=" + id + "&download=" + download;
            log.debug("Creating rest connection for URI: " + url);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
        }
        return Boolean.FALSE;
    }

}

package org.neuromorpho.literature.download.communication;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CrosRefCommunication {

    
    @Value("${uriCrosRef}")
    private String uri;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
  
     public String downloadPDF(String doi, String id) {
      
        String url = uri + "?doi=" + doi + "&id=" + id;
        log.debug("Creating rest connection for URI: " + url);
        RestTemplate restTemplate = new RestTemplate();                
        return restTemplate.getForObject(url, String.class);
    }
     

}

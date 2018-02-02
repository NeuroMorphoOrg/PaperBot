package org.neuromorpho.literature.download.communication;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LiteratureCommunication {

    @Value("${uriLiterature}")
    private String uri;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ArticlePage getArticleList(Integer page, String status) {
        
        String url = uri + "/query?collection=" + status + "&page=" + page + 
                "&pdfPath=$exist:false&doi=$exist:true";
        log.debug("Creating rest connection for URI: " + url);
        RestTemplate restTemplate = new RestTemplate();
        ArticlePage articlePage = restTemplate.getForObject(url, ArticlePage.class);

        return articlePage;
    }
    
     public void updateArticlePdf(String id, String downlodedPath) {
        Map<String, Object> article = new HashMap();
        article.put("pdfPath", downlodedPath);
        String url = uri + "/update/" + id;
        log.debug("Creating rest connection for URI: " + url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(url, article);
    }
     

}

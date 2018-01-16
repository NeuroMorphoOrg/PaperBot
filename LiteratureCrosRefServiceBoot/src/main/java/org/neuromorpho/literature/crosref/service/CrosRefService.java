package org.neuromorpho.literature.crosref.service;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.neuromorpho.literature.crosref.communication.CrosRefConnection;
import org.neuromorpho.literature.crosref.model.Article;
import org.neuromorpho.literature.crosref.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CrosRefService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${folder}")
    private String folder;

    @Autowired
    protected CrosRefConnection crosRefConnection;

    public Article retrieveCrosRefArticleData(String doi) throws Exception {
        Article article = new Article();

        Map message = crosRefConnection.findMetadataFromDOI(doi);
        ArrayList<String> titles = (ArrayList) message.get("title");
        article.setTitle(titles.get(0));

        ArrayList<Map> authors = (ArrayList) message.get("author");
        List<Author> authorList = new ArrayList();

        for (Map a : authors) {
            String given = (String) a.get("given");
            String family = (String) a.get("family");
            Author author = new Author(given + " " + family, null);
            authorList.add(author);
        }
        article.setAuthorList(authorList);
        article.setJournal((String) message.get("container-title"));
        Map created = (Map) message.get("created");
        String sortDateStr = (String) created.get("date-time");
        article.setPublishedDate(this.tryParseDate(sortDateStr));

        ArrayList<Map> links = (ArrayList) message.get("link");
        for (Map link : links) {
            String contentType = (String) link.get("content-type");
            if (contentType.equals("application/pdf")) {
                article.setPdfLink((String) link.get("URL"));
            }
        }
        article.setDoi(doi);
        log.debug("Article found: " + article.toString());
        return article;
    }

    public String downloadPDFFromDOI(String doi, String id) throws Exception {
        Map message = crosRefConnection.findMetadataFromDOI(doi);
        ArrayList<Map> links = (ArrayList) message.get("link");
        ArrayList<String> titles = (ArrayList) message.get("title");
        Map created = (Map) message.get("created");
        String sortDateStr = (String) created.get("date-time");
        String[] filePath = sortDateStr.split("-");
        String completePath = folder + filePath[0] + File.separator + filePath[1] + File.separator;

        for (Map link : links) {
            String contentType = (String) link.get("content-type");
            if (contentType.equals("application/pdf")) {
                File dir = new File(completePath);
                dir.mkdirs();

                //Download pdf
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(
                        new ByteArrayHttpMessageConverter());

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<byte[]> response = restTemplate.getForEntity(
                        (String) link.get("URL"), byte[].class);
                
                while (response.getStatusCode() == HttpStatus.SEE_OTHER ||
                        response.getStatusCode() == HttpStatus.FOUND) {
                    response = restTemplate.getForEntity(
                        response.getHeaders().getLocation(), byte[].class);
                    
                }
                if (response.getStatusCode() == HttpStatus.OK) {
                    Files.write(Paths.get(completePath + id + ".pdf"), response.getBody());
                }

//                URL url = new URL((String) link.get("URL"));
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setInstanceFollowRedirects(true);
//                InputStream in = conn.getInputStream();
//           
//                Files.copy(in, Paths.get(completePath + id + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
//                in.close();
            }
        }
        return "";
    }

    protected Date tryParseDate(String dateStr) {
        String[] formatStrings = {"yyyy-MM-dd"};
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString, Locale.US).parse(dateStr.toLowerCase());
            } catch (ParseException e) {
            }
        }
        return null;
    }

}

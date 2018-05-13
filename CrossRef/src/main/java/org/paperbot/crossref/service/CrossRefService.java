package org.paperbot.crossref.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.paperbot.crossref.communication.CrossRefConnection;
import org.paperbot.crossref.model.Article;
import org.paperbot.crossref.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CrossRefService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${token}")
    private String token;

    @Autowired
    GridFsOperations gridOperations;

    @Autowired
    protected CrossRefConnection crossRefConnection;

    public Article retrieveArticleData(String doi) throws Exception {
        Article article = new Article();

        Map message = crossRefConnection.findMetadataFromDOI(doi);
        log.debug("Message from CrossRef: " + message);
        List<String> titles = (ArrayList) message.get("title");
        article.setTitle(titles.get(0));

        ArrayList<Map> authors = (ArrayList) message.get("author");
        List<Author> authorList = new ArrayList();
        if (authors != null) {
            for (Map a : authors) {
                String given = (String) a.get("given");
                String family = (String) a.get("family");
                Author author = new Author(given + " " + family, null);
                authorList.add(author);
            }
        }
        article.setAuthorList(authorList);
        List<String> journalList = (List) message.get("container-title");
        if (journalList != null && journalList.size() > 0) {
            article.setJournal(journalList.get(0));
        }
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

    public Boolean downloadPDFFromDOI(String doi, String id, Boolean download) throws Exception {
        Map message = crossRefConnection.findMetadataFromDOI(doi);
        ArrayList<Map> links = (ArrayList) message.get("link");
        GridFSFile file = null;
        String idPdf = null;
        Boolean exists = Boolean.FALSE;
        if (links != null) {
            for (Map link : links) {
                String contentType = (String) link.get("content-type");
                if (contentType.equals("application/pdf")
                        || contentType.equals("unspecified")) {
                    try {

                        //Download pdf
                        RestTemplateTimeout restTemplate = new RestTemplateTimeout();
                        String url = (String) link.get("URL");
                        log.debug("Accesing download pdf: " + url);

                        HttpHeaders headersResponse = restTemplate.headForHeaders(url);

                        log.debug("Content type fo the url: " + headersResponse.getContentType()
                                + " location :" + headersResponse.getLocation());

                        while (!headersResponse.getContentType().toString().equals(MediaType.APPLICATION_PDF_VALUE)
                                && headersResponse.getLocation() != null) {
                            log.debug("Content type fo the url: " + headersResponse.getContentType()
                                    + " location :" + headersResponse.getLocation());
                            headersResponse = restTemplate.headForHeaders(headersResponse.getLocation());
//                       
//                        

//                        while (response.getStatusCode() == HttpStatus.SEE_OTHER
//                                || response.getStatusCode() == HttpStatus.FOUND
//                                || response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY) {
//                            response = restTemplate.exchange(
//                                    response.getHeaders().getLocation(), HttpMethod.GET, entity, byte[].class);
//                        }
                        }

                        if (headersResponse.getContentType().toString().equals(MediaType.APPLICATION_PDF_VALUE)) {
                            log.debug("PDF found ");
                            exists = true;
                            if (download) {
                                log.debug("Downalong file");
                                GridFSDBFile dbfile = this.findPDF(id);
                                if (dbfile == null) { // if is not already in DB download
                                    restTemplate.getMessageConverters().add(
                                            new ByteArrayHttpMessageConverter());

                                    HttpHeaders headers = new HttpHeaders();
                                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
                                    headers.set("CR-Clickthrough-Client-Token", token);

                                    HttpEntity<String> entity = new HttpEntity<>(headers);
                                    ResponseEntity<byte[]> response = restTemplate.exchange(
                                            url, HttpMethod.GET, entity, byte[].class);
                                    if (response.getStatusCode() == HttpStatus.OK) {
                                        byte[] pdf = response.getBody();
                                        file = gridOperations.store(new ByteArrayInputStream(pdf), id);
                                        if (file != null) {
                                            idPdf = file.getId().toString();
                                        }
                                    }

                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.debug("CrossRef link not working, trying other links");
                    }

                } else {
                    log.warn("Article in CrossRef but no pdf associated");
                }
            }
        }
        return exists;
    }

    public GridFSDBFile findPDF(String id) {
        log.debug("Reading PDF filename: " + id);
        Query query = new Query();
        Criteria criteria = Criteria.where("filename").is(id);
        query.addCriteria(criteria);
        GridFSDBFile dbfile = gridOperations.findOne(query);
        return dbfile;
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
    
    public class RestTemplateTimeout extends RestTemplate {
    public RestTemplateTimeout() {
        if (getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(10 * 1000);
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setReadTimeout(10 * 1000);
        } else if (getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setReadTimeout(10 * 1000);
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(10 * 1000);
        }
    }
}

}

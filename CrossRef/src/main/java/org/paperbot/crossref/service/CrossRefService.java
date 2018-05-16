package org.paperbot.crossref.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
        GridFSDBFile dbfile = this.findPDF(id);
        Boolean exists = Boolean.FALSE;
        if (dbfile == null) { // if is not already in DB download
            Map message = crossRefConnection.findMetadataFromDOI(doi);
            ArrayList<Map> links = (ArrayList) message.get("link");
            if (links != null) {
                for (Map link : links) {
                    String contentType = (String) link.get("content-type");
                    if (contentType.equals("application/pdf")
                            || contentType.equals("unspecified")) {
                        String url = (String) link.get("URL");
                        exists = this.downloadPDF(url, download, id, null);

                    }
                }
            }
            String url = (String) message.get("URL");
            if (url != null && !exists) {
                exists = this.extract(url, download, id);
            }
        } else {
            exists = Boolean.TRUE;
            log.debug("PDF downloaded in previous searches");
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

    private Boolean extract(String url, Boolean download, String id) throws IOException {
        Response res = Jsoup.connect(url)
                .timeout(60 * 1000)
                .followRedirects(true)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                .execute();

        Document doc = res.parse();
        Map<String, String> cookies = res.cookies();
        Element element = doc.select("meta[name=citation_pdf_url]").first();
        return this.downloadPDF(element.attr("content"), download, id, cookies);

    }

    private Boolean downloadPDF(String url, Boolean download, String id, Map<String, String> cookies) {
        Boolean exists = Boolean.FALSE;
        try {

            //Download pdf
            RestTemplateTimeout restTemplate = new RestTemplateTimeout();
            log.debug("Accesing download pdf: " + url);
            restTemplate.getMessageConverters().add(
                    new ByteArrayHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            headers.set("CR-Clickthrough-Client-Token", token);
            if (cookies != null) {
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    headers.add("Cookie", entry.getKey() + "=" + entry.getValue());

                }
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, byte[].class);

            log.debug("Content type for the url: " + response.getHeaders().getContentType()
                    + " location :" + response.getHeaders().getLocation());

            while (response.getStatusCode() == HttpStatus.SEE_OTHER
                    || response.getStatusCode() == HttpStatus.FOUND
                    || response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY) {
                log.debug("Content type fo the url: " + response.getHeaders().getContentType()
                        + " location :" + response.getHeaders().getLocation());
                response = restTemplate.exchange(response.getHeaders().getLocation(), HttpMethod.GET, entity, byte[].class);

            }

            if (response.getHeaders().getContentType().toString().contains(MediaType.APPLICATION_PDF_VALUE.toString())
                    || response.getHeaders().getContentType().toString().contains(MediaType.APPLICATION_OCTET_STREAM.toString())) {
                log.debug("PDF found ");
                exists = Boolean.TRUE;
                if (download) {
                    log.debug("Downloading file");

                    if (response.getStatusCode() == HttpStatus.OK) {
                        byte[] pdf = response.getBody();
                        GridFSFile file = gridOperations.store(new ByteArrayInputStream(pdf), id);
                        if (file != null) {
                            String idPdf = file.getId().toString();
                        }
                    }

                }
            }

        } catch (Exception ex) {
            log.debug("CrossRef link not working, trying other links");
            log.error("Error:", ex);
        }
        return exists;

    }

}

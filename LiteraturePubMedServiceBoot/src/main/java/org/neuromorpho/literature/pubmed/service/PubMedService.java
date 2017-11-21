package org.neuromorpho.literature.pubmed.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.neuromorpho.literature.pubmed.exceptions.PubMedException;
import org.neuromorpho.literature.pubmed.model.Article;
import org.neuromorpho.literature.pubmed.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PubMedService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${uri}")
    private String uri;

    public Article retrievePubMedArticleData(String pmid, String db) throws Exception {
        log.debug("Retrieveng data from PubMed for article pmid: " + pmid);
        Article article = this.fillArticleData(pmid, db);
        this.fillAuthorList(pmid, article);
        return article;

    }

    private Article fillArticleData(String pmid, String db) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = uri
                + "/esummary.fcgi?"
                + "db=" + db
                + "&retmode=json"
                + "&id=" + pmid;
        log.debug("Accesing pubmed using url: " + pmid);
        Map<String, Object> articleMap = restTemplate.getForObject(url, Map.class);
        Article article = new Article();
        Map result = (HashMap) articleMap.get("result");
        if (result == null) {
            List<String> error = (List) articleMap.get("esummaryresult");
            throw new PubMedException(error.get(0));
        }
        ArrayList<String> uids = (ArrayList) result.get("uids");
        if (uids.isEmpty()) {
            throw new Exception("Unknown pmid not found in PubMed: " + pmid);
        }
        article.setPmid(uids.get(0));
        Map articleValues = (HashMap) result.get(uids.get(0));
        article.setTitle(getCorrectedName((String) articleValues.get("title")));
        article.setJournal(getCorrectedName((String) articleValues.get("fulljournalname")));
        String sortDateStr = (String) articleValues.get("sortpubdate");
        if (sortDateStr != null && !sortDateStr.isEmpty()) {
            try {
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

                article.setPublishedDate(format.parse(sortDateStr));
            } catch (ParseException ex) {
            }
        }
        String locationId = (String) articleValues.get("elocationid");
        if (locationId != null && locationId.contains("doi")) {
            article.setDoi(locationId.split(":")[1].trim());
        }
        return article;
    }

    private void fillAuthorList(String pmid, Article article) {
        RestTemplate restTemplate = new RestTemplate();

        String xml = restTemplate.getForObject(
                uri
                + "/efetch.fcgi?"
                + "db=pubmed"
                + "&id=" + pmid
                + "&retmode=xml",
                String.class);

        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        List<Author> authorList = new ArrayList();

        for (Element a : doc.select("Author")) {
            Element e = a.select("Affiliation").first();
            String email = null;
            if (e != null && e.text().contains("@")) {
                email = e.text().substring(e.text().lastIndexOf(" ") + 1, e.text().length());
            }
            Element fn = a.select("ForeName").first();
            Element ln = a.select("LastName").first();
            if (fn != null && ln != null) {
                Author author = new Author(fn.text() + " " + ln.text(), email);
                authorList.add(author);
            }
        }

        article.setAuthorList(authorList);
    }

    private String getCorrectedName(String name) {
        if (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
        }
        return name.replace("&amp;", "&");
    }

}

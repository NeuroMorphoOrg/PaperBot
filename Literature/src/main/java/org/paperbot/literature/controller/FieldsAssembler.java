package org.paperbot.literature.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.paperbot.literature.model.article.ArticleCollection.ArticleStatus;

public class FieldsAssembler {


    public List<String> getFieldList() {
        List<String> fieldList = new ArrayList();
        fieldList.add("publishedYear");
        fieldList.add("ocYear");
        fieldList.add("pmid");
        fieldList.add("status");
        fieldList.add("statusDetails");
        fieldList.add("usage");
        fieldList.add("species");
        return fieldList;
    }

    public String getField(String field) {
        switch (field) {
            case "usage":
                return "dataUsage";
            case "publishedYear":
                return "publishedDate";
            case "ocYear":
                return "evaluatedDate";
            default:
                return field;
        }
    }

    public Map<String, List<String>> getFieldQuery(
            String publishedYear,
            String ocYear,
            String ltDate,
            String gtDate,
            String pmid,
            String species,
            String usage,
            String email,
            String articleStatus) {
        Map<String, List<String>> result = new HashMap();

        if (ltDate != null) {
            List<String> value = new ArrayList();
            value.add(ltDate);
            result.put("ltDate", value);
        }
        if (gtDate != null) {
            List<String> value = new ArrayList();
            value.add(gtDate);
            result.put("gtDate", value);
        }
        if (publishedYear != null) {
            List<String> value = new ArrayList();
            value.add(publishedYear);
            result.put("publishedDate", value);
        }
        if (ocYear != null) {
            List<String> value = new ArrayList();
            value.add(ocYear);
            result.put("evaluatedDate", value);
        }
        if (pmid != null) {
            List<String> value = new ArrayList();
            value.add(pmid);
            result.put("pmid", value);
        }
        if (usage != null) {
            List<String> value = new ArrayList();
            value.add(usage);
            result.put("dataUsage", value);
        }
        if (email != null) {
            List<String> value = new ArrayList();
            value.add(email);
            result.put("authorList.email", value);
        }
        if (articleStatus != null) {
            List<String> value = new ArrayList();
            value.add(ArticleStatus.getArticleStatus(articleStatus).toString());
            result.put("articleStatus", value);
        }
        return result;
    }

    public Date getLastDay(String dateStr) {
        Date result = null;
        if (dateStr != null) {
            String[] dateListStr = dateStr.split("-");

            Calendar date = Calendar.getInstance();   //current date
            date.set(Calendar.SECOND, date.getActualMaximum(Calendar.SECOND));
            date.set(Calendar.MINUTE, date.getActualMaximum(Calendar.MINUTE));
            date.set(Calendar.HOUR_OF_DAY, date.getActualMaximum(Calendar.HOUR_OF_DAY));
            date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
            date.set(Calendar.MONTH, Integer.parseInt(dateListStr[1]) - 1);
            date.set(Calendar.YEAR, Integer.parseInt(dateListStr[0]));
            result = date.getTime();
        }
        return result;
    }

    public Date getFirstDay(String dateStr) {
        Date result = null;
        if (dateStr != null) {
            String[] dateListStr = dateStr.split("-");

            Calendar date = Calendar.getInstance();   //current date
            date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND));
            date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE));
            date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY));
            date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
            date.set(Calendar.MONTH, Integer.parseInt(dateListStr[1]) - 1);
            date.set(Calendar.YEAR, Integer.parseInt(dateListStr[0]));
            result = date.getTime();
        }
        return result;
    }

}

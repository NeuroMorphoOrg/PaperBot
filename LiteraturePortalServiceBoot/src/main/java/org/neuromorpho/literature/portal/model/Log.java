package org.neuromorpho.literature.portal.model;

import java.util.Date;

public class Log {

    private Date date;
    private Integer total;
    private Integer searchPeriod;
    
    public Log(Date date, Integer total) {
        this.date = date;
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSearchPeriod() {
        return searchPeriod;
    }

    public void setSearchPeriod(Integer searchPeriod) {
        this.searchPeriod = searchPeriod;
    } 

}

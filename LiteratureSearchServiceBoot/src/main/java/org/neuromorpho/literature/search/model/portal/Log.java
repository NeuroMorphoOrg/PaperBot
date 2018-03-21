package org.neuromorpho.literature.search.model.portal;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "log")
public class Log {

    @Id
    private String id;

    private Date start;
    private Date stop;

    public Log() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }
    
    public void statLogging() {
        this.start = new Date();
    }
    
    public void endLogging() {
        this.stop = new Date();
    }
   
}

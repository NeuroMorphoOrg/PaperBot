/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.model;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "log")
public class PortalLog {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;
    private List<Log> log;

    public PortalLog() {
    }

    public PortalLog(String name) {
        this.name = name;
    }

    public PortalLog(String name, List<Log> log) {
        this.name = name;
        this.log = log;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Log> getLog() {
        return log;
    }

    public void setLog(List<Log> log) {
        this.log = log;
    }

    public void updateLog(Log log){
        if (this.log == null){
            this.log = new ArrayList();
        }
        this.log.add(log);
    }
}

package org.neuromorpho.literature.portal.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "keyword")
public class KeyWord {

    @Id
    private ObjectId id;

    private String name;
    private String collection;
    private String usage;
    
    @PersistenceConstructor
   

    public KeyWord() {
    }

    public KeyWord(String name, String collection, String usage) {
        this.name = name;
        this.collection = collection;
        this.usage = usage;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

}

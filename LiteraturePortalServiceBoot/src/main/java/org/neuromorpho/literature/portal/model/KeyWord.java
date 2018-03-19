package org.neuromorpho.literature.portal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "keyword")
public class KeyWord {

    @Id
    private String id;
    
    @Indexed(unique = true, sparse = false)
    private String name;
    private String collection;

    @PersistenceConstructor
    public KeyWord() {
    }

    public KeyWord(String name, String collection) {
        this.name = name;
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}

package org.neuromorpho.literature.metadata.model;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metadata.values")
public class MetadataValues {

    @Id
    private ObjectId id;
    private String type;
    private String name;
    private List<String> synonyms;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public String toString() {
        return "MetadataValues{" + "id=" + id + ", type=" + type 
                + ", name=" + name + ", synonyms=" + synonyms + '}';
    }

  

}

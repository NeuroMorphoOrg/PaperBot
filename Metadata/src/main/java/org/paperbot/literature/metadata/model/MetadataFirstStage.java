package org.paperbot.literature.metadata.model;

import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metadata")
public class MetadataFirstStage {

    @Id
    private ObjectId id;
    private Map<String, Object> attributes;

    public MetadataFirstStage(ObjectId id, Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public MetadataFirstStage() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "MetadataFirstStage{" + "id=" + id 
                + ", attributes=" + attributes + '}';
    }

}

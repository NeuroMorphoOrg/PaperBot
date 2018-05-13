package org.paperbot.literature.search.model.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public List<KeyWord> extractORs() {
        List<KeyWord> keyWordList = new ArrayList();
        List<String> wordList = Arrays.asList(this.name.split(" OR "));
        for (String word : wordList) {
            KeyWord newKeyWord = new KeyWord();
            newKeyWord.setCollection(this.collection);
            newKeyWord.setName(word);
            keyWordList.add(newKeyWord);
        }
        return keyWordList;
    }
}

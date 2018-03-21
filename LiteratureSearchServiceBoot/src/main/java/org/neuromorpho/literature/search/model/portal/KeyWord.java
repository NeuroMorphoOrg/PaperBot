package org.neuromorpho.literature.search.model.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyWord {
    
    private String name;
    private String collection;
    
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

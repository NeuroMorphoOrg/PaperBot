
package org.paperbot.literature.search.model.article;

public class Search implements java.io.Serializable {

    private String source;
    private String keyWord;

    public Search() {
    }

    public Search(String source, String keyWord) {
        this.source = source;
        this.keyWord = keyWord;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return "Search{" + "source=" + source + ", keyWord=" + keyWord + '}';
    }
    
    

}

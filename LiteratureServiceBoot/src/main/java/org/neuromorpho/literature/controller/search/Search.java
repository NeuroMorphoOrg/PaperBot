/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller.search;

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

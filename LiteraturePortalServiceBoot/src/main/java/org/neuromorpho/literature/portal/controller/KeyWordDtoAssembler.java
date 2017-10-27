/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.portal.controller;

import org.neuromorpho.literature.portal.model.KeyWord;

public class KeyWordDtoAssembler {

    public KeyWordDto createKeyWordDto(KeyWord keyWord) {
        KeyWordDto keyWordDto = new KeyWordDto();
        keyWordDto.setName(keyWord.getName());
        keyWordDto.setUsage(keyWord.getUsage());
        keyWordDto.setCollection(keyWord.getCollection());
        return keyWordDto;
    }

    public KeyWord createKeyWord(KeyWordDto keyWordDto) {
        KeyWord keyWord = new KeyWord();
        keyWord.setName(keyWordDto.getName());
        keyWord.setCollection(keyWordDto.getCollection());
        keyWord.setUsage(keyWordDto.getUsage());
        return keyWord;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller;

import org.neuromorpho.literature.model.article.Author;

public class AuthorDtoAssembler implements java.io.Serializable {

    public AuthorDto createAuthorDto(Author author) {
        String email = "";
        if (author.getEmail() != null) {
            email = author.getEmail();
        }
        return new AuthorDto(author.getName(), email);

    }

    public Author createAuthor(AuthorDto authorDto) {
        return new Author(authorDto.getName(), authorDto.getEmail());
    }

}

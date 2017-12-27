/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuromorpho.literature.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.neuromorpho.literature.exceptions.MissingDataException;
import org.neuromorpho.literature.model.article.Article;
import org.neuromorpho.literature.model.article.ArticleCollection;
import org.neuromorpho.literature.model.article.ArticleCollection.ArticleStatus;
import org.neuromorpho.literature.model.article.Author;

public class ArticleDtoAssembler {

    private final SearchPortalDtoAssembler searchPortalDtoAssembler = new SearchPortalDtoAssembler();
    private final AuthorDtoAssembler authorDtoAssembler = new AuthorDtoAssembler();

    public ArticleDto createArticleDto(Article article) {
        ArticleDto articleDto = new ArticleDto();
        if (article.getId() != null) {
            articleDto.setId(article.getId().toString());
        }
        List<AuthorDto> authorList = new ArrayList();
        for (Author author : article.getAuthorList()) {
            authorList.add(authorDtoAssembler.createAuthorDto(author));
        }
        articleDto.setAuthorList(authorList);
        articleDto.setDoi(article.getDoi());
        articleDto.setJournal(article.getJournal());
        articleDto.setPmid(article.getPmid());
        articleDto.setPublishedDate(article.getPublishedDate());
        articleDto.setTitle(article.getTitle());
        articleDto.setLink(article.getLink());
        articleDto.setEvaluatedDate(article.getEvaluatedDate());

        return articleDto;
    }

    public ArticleDto createArticleDto(ArticleCollection articleCollection) {
        if (articleCollection != null) {
            ArticleDto articleDto = new ArticleDto();

            Article article = articleCollection.getArticle();
            if (articleCollection.getArticleStatus() != null) {
                articleDto.setArticleStatus(articleCollection.getArticleStatus().getStatus());
            }
            articleDto.setId(article.getId().toString());
            List<AuthorDto> authorList = new ArrayList();
            for (Author author : article.getAuthorList()) {
                authorList.add(authorDtoAssembler.createAuthorDto(author));
            }
            articleDto.setAuthorList(authorList);
            articleDto.setDoi(article.getDoi());
            articleDto.setJournal(article.getJournal());
            articleDto.setPmid(article.getPmid());
            articleDto.setPublishedDate(article.getPublishedDate());
            articleDto.setTitle(article.getTitle());
            articleDto.setLink(article.getLink());
            articleDto.setEvaluatedDate(article.getEvaluatedDate());
            articleDto.setSearchPortal(
                    searchPortalDtoAssembler.createSearchPortalDto(article.getSearchPortal()));
            articleDto.setUsage(article.getDataUsage());
            return articleDto;

        } else {
            return new ArticleDto();
        }
    }

    public Article createArticle(ArticleDto articleDto) {
        if (articleDto.getTitle() == null) {
            throw new MissingDataException("article data");
        }
        Article article = new Article();
        article.setPmid(articleDto.getPmid());
        article.setTitle(articleDto.getTitle());
        article.setDoi(articleDto.getDoi());
        article.setLink(articleDto.getLink());
        article.setJournal(articleDto.getJournal());
        article.setOcDate(new Date());
        article.setPublishedDate(articleDto.getPublishedDate());
        List<Author> authorList = new ArrayList();
        for (AuthorDto author : articleDto.getAuthorList()) {
            authorList.add(authorDtoAssembler.createAuthor(author));
        }
        article.setAuthorList(authorList);
        article.setDataUsage(articleDto.getUsage());
        article.setAbstact(articleDto.getAbstact());
        return article;
    }

}

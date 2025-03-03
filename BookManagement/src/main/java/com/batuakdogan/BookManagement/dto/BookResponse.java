package com.batuakdogan.BookManagement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private LocalDateTime publicationDate;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BookResponse fromBook(com.batuakdogan.BookManagement.model.Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setGenre(book.getGenre());
        response.setPublicationDate(book.getPublicationDate());
        response.setAuthorId(book.getAuthor().getId());
        response.setAuthorUsername(book.getAuthor().getUsername());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }
} 
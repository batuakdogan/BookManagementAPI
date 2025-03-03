package com.batuakdogan.BookManagement.repository;

import com.batuakdogan.BookManagement.model.Book;
import com.batuakdogan.BookManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(User author);
    List<Book> findByAuthorId(Long authorId);
} 
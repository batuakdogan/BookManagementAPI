package com.batuakdogan.BookManagement.controller;

import com.batuakdogan.BookManagement.dto.BookRequest;
import com.batuakdogan.BookManagement.dto.BookResponse;
import com.batuakdogan.BookManagement.model.Book;
import com.batuakdogan.BookManagement.model.User;
import com.batuakdogan.BookManagement.repository.BookRepository;
import com.batuakdogan.BookManagement.repository.UserRepository;
import com.batuakdogan.BookManagement.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookRequest bookRequest,
                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            log.info("Attempting to create book with title: {} for user: {}", bookRequest.getTitle(), currentUser.getUsername());
            
            User author = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            log.debug("Found author: {}", author.getUsername());

            Book book = new Book();
            book.setTitle(bookRequest.getTitle());
            book.setDescription(bookRequest.getDescription());
            book.setGenre(bookRequest.getGenre());
            book.setAuthor(author);
            
            log.debug("Created book object: {}", book);

            Book savedBook = bookRepository.save(book);
            log.info("Successfully created book with id: {}", savedBook.getId());
            
            return ResponseEntity.ok(BookResponse.fromBook(savedBook));
        } catch (Exception e) {
            log.error("Error creating book: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error creating book: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                      @Valid @RequestBody BookRequest bookRequest,
                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            // Check if the current user is the author of the book
            if (!book.getAuthor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("You can only update your own books");
            }

            book.setTitle(bookRequest.getTitle());
            book.setDescription(bookRequest.getDescription());
            book.setGenre(bookRequest.getGenre());

            Book updatedBook = bookRepository.save(book);
            return ResponseEntity.ok(BookResponse.fromBook(updatedBook));
        } catch (Exception e) {
            log.error("Error updating book: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error updating book: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id,
                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            // Check if the current user is the author of the book
            if (!book.getAuthor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("You can only delete your own books");
            }

            bookRepository.delete(book);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting book: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error deleting book: " + e.getMessage());
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getBooksByAuthor(@PathVariable Long authorId) {
        try {
            List<Book> books = bookRepository.findByAuthorId(authorId);
            List<BookResponse> responses = books.stream()
                    .map(BookResponse::fromBook)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching books by author: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error fetching books: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            return ResponseEntity.ok(BookResponse.fromBook(book));
        } catch (Exception e) {
            log.error("Error fetching book: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error fetching book: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();
            List<BookResponse> responses = books.stream()
                    .map(BookResponse::fromBook)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching all books: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("Error fetching books: " + e.getMessage());
        }
    }
} 
package com.ticketing.repository;

import com.ticketing.model.Comment;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    List<Comment> findByAuthor(User author);
    List<Comment> findByTicketAndIsInternalFalseOrderByCreatedAtAsc(Ticket ticket);
    long countByTicket(Ticket ticket);
}
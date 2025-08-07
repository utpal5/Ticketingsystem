package com.ticketing.repository;

import com.ticketing.model.Rating;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByTicket(Ticket ticket);
    List<Rating> findByRatedBy(User ratedBy);
    
    @Query("SELECT AVG(r.rating) FROM Rating r")
    Double findAverageRating();
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.ticket.assignee = :assignee")
    Double findAverageRatingByAssignee(User assignee);
}
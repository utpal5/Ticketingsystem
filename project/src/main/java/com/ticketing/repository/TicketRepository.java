package com.ticketing.repository;

import com.ticketing.model.Priority;
import com.ticketing.model.Ticket;
import com.ticketing.model.TicketStatus;
import com.ticketing.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreator(User creator);
    List<Ticket> findByAssignee(User assignee);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(Priority priority);
    List<Ticket> findByCreatorOrderByCreatedAtDesc(User creator);
    List<Ticket> findByAssigneeOrderByCreatedAtDesc(User assignee);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:creatorId IS NULL OR t.creator.id = :creatorId) AND " +
           "(:assigneeId IS NULL OR t.assignee.id = :assigneeId)")
    Page<Ticket> findTicketsWithFilters(@Param("search") String search,
                                       @Param("status") TicketStatus status,
                                       @Param("priority") Priority priority,
                                       @Param("creatorId") Long creatorId,
                                       @Param("assigneeId") Long assigneeId,
                                       Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE t.creator = :user AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    Page<Ticket> findUserTicketsWithFilters(@Param("user") User user,
                                           @Param("search") String search,
                                           @Param("status") TicketStatus status,
                                           @Param("priority") Priority priority,
                                           Pageable pageable);
    
    long countByStatus(TicketStatus status);
    long countByPriority(Priority priority);
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
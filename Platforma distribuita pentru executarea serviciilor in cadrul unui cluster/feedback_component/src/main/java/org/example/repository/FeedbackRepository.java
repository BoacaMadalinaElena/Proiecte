package org.example.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.dto.FeedbackDto;

@Repository
@Transactional
public interface FeedbackRepository extends JpaRepository<FeedbackDto,String> {
}

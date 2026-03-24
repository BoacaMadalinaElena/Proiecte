package org.example.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.example.dto.*;

import java.util.List;

@Repository
public interface CodeMessageRepository extends JpaRepository<CodeMessage,String> {
    public List<CodeMessage> findByCodeRecord(String id);
    @Transactional
    @Modifying
    @Query("DELETE FROM CodeMessage c WHERE c.codeRecord = :id")
    public void deleteByCodeRecord(String id);
}

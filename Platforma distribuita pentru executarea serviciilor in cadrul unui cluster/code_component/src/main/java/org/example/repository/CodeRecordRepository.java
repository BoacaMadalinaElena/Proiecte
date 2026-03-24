package org.example.repository;

import org.example.dto.CodeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRecordRepository extends JpaRepository<CodeRecord,String> {
}

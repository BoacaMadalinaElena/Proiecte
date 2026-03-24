package org.example.repository;

import org.example.dto.LogsMessageExecutionNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface LogRepositoryExecutionNode extends MongoRepository<LogsMessageExecutionNode, String> {
    @Query("{'localDateTime': { $regex: ?0 }, 'address': { $regex: ?1 }, 'type': { $regex: ?2 }, 'userId': { $regex: ?3 }}")
    List<LogsMessageExecutionNode> findByCriteria(String time, String address, String type, String userId);
}
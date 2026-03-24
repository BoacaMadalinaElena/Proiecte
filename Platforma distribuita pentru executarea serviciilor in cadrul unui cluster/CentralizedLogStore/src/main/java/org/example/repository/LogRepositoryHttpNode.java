package org.example.repository;

import org.example.dto.LogsMessageHttpNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface LogRepositoryHttpNode extends MongoRepository<LogsMessageHttpNode, String> {
    @Query("{'localDateTime': { $regex: ?0 }, 'address': { $regex: ?1 }, 'type': { $regex: ?2 },'typeNode': { $regex: ?3 }}")
    List<LogsMessageHttpNode> findByCriteria(String time, String address, String type, String typeNode);
}
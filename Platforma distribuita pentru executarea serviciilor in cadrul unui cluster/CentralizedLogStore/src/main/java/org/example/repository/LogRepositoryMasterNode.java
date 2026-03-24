package org.example.repository;

import org.example.dto.LogsMessageMasterNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogRepositoryMasterNode extends MongoRepository<LogsMessageMasterNode, String> {
    @Query("{'localDateTime': { $regex: ?0 }, 'address': { $regex: ?1 }, 'type': { $regex: ?2 }}")
    List<LogsMessageMasterNode> findByCriteria(String time, String address, String type);
}
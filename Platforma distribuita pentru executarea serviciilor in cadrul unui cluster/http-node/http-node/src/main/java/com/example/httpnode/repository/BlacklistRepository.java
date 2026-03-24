package com.example.httpnode.repository;

import com.example.httpnode.dto.BlackListDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends CrudRepository<BlackListDTO, String> {
}

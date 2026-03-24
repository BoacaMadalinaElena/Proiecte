package com.example.httpnode.repository;

import com.example.httpnode.dto.UserDto;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface IDMRepository extends JpaRepository<UserDto,String> {
    public Optional<UserDto> findByEmail(String email);
    public Optional<UserDto> findByUsername(String username);

    public Optional<UserDto> findUserById(String id);
    @Modifying
    @Query("UPDATE UserDto u SET u.password = :password WHERE u.id = :id")
    void updatePasswordById(@Param("id") String id, @Param("password") String password);

    @Modifying
    @Query("UPDATE UserDto u SET u.firstName = :firstName, u.lastName = :lastName, u.username = :username, u.description = :description WHERE u.id = :id")
    void updateUserInfo(@Param("id") String id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("username") String username, @Param("description") String description);
}

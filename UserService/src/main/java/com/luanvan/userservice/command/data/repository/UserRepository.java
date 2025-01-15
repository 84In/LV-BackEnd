package com.luanvan.userservice.command.data.repository;

import com.luanvan.userservice.command.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u JOIN FETCH u.addresses WHERE u.id= :userId")
    User findByUserId(@Param("userId") String userId);
}

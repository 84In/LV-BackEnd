package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u JOIN FETCH u.addresses WHERE u.id= :userId")
    User findByUserId(@Param("userId") String userId);

    boolean existsByUsername(@NotBlank @Size(max = 50) String username);
}

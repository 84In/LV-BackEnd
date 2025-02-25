package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.Cart;
import com.luanvan.userservice.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    boolean existsByUser(User user);

   Optional<Cart> findByUser(User user);
}

package com.luanvan.userservice.command.data.repository;

import com.luanvan.userservice.command.data.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}

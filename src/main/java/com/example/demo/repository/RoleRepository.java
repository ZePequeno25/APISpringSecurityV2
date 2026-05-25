package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Consulta uma role pelo nome, evitando criar roles duplicadas no cadastro.
    Optional<Role> findByName(RoleName name);
}

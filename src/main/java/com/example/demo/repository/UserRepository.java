package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Consulta o usuario pelo email. O Spring Data JPA gera a query automaticamente pelo nome do metodo.
    Optional<User> findByEmail(String email);
}

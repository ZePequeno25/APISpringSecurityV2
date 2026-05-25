package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // Chave primaria da tabela users, gerada automaticamente pelo MySQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email usado como identificador de login. Deve ser unico no banco.
    @Column(unique = true)
    private String email;

    // Senha criptografada com BCrypt antes de ser salva.
    private String password;

    // Lista de permissoes do usuario. EAGER carrega as roles junto com o usuario para autenticar/autorizacao.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            // Tabela intermediaria que liga usuarios e roles.
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}

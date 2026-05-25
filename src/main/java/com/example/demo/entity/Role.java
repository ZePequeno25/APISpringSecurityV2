package com.example.demo.entity;

import com.example.demo.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    // Chave primaria da tabela roles, gerada automaticamente pelo MySQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da role salvo como texto no banco, por exemplo ROLE_CUSTOMER.
    @Enumerated(EnumType.STRING)
    private RoleName name;
}

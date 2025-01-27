// User will have multiple roles, so we need to create a Role entity to store the roles in the database

package com.learning.security.models;

import com.learning.security.enums.ERole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "roles")
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Integer id;

    // Role can be only of the types defined in the enum ERoles
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @NotNull        // jakarta validation
    @NonNull        // for lombok - I needed parameterized constructer Role(ERole name) for the method of
    private ERole name;

    public static Role of(ERole name) {
        return new Role(name);
    }

}

package com.learning.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Instructions
 * 1. Table name users
 * Fields:
 *  @Integer id         - primary key, auto generated
 *  @String username    - not blank, unique, max 20 characters
 *  @String email       - not blank, unique, max 50 characters
 *  @String password    - not blank, max 120 characters (will be encrypted)
 *  @HashSet roles      - many to many relationship with Role entity, fetch type lazy, cascade delete
 *  @LocalDateTime createdAt - not blank, column definition default current timestamp
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotBlank
    private Integer id;

    @NotBlank
    @Column(length = 20)
    @NotNull
    private String username;

    @NotBlank
    @Column(length = 50)
    @NotNull
    private String email;

    @NotBlank
    @Column(length = 120)
    @NotNull
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})  // Roles will be fetched only when they are accessed
    @JoinTable(name = "user_roles"
            // user_id is the foreign key from the user table
            , joinColumns = @JoinColumn(name = "user_id")
            // role_id is the foreign key from the role table
            , inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

}

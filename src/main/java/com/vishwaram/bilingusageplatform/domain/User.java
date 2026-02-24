//package com.vishwaram.bilingusageplatform.domain;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//public class User {
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id")
//    private Tenant tenant;
//    private String userName;
//    private String firstName;
//    private String lastName;
//    private LocalDate dateCreated;
//    private boolean isActive;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private final Set<Role> roles;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private final Set<Permission> permissions;
//
//    public User() {
//        roles = new HashSet<>();
//        permissions = new HashSet<>();
//    }
//
//    public void addRole(Role role) {
//        this.roles.add(role);
//    }
//
//    public void addPermission(Permission permission) {
//        this.permissions.add(permission);
//    }
//}

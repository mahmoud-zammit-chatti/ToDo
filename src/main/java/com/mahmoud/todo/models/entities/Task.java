package com.mahmoud.todo.models.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private String taskId;
    @Column(nullable = false)
    private String taskName;
    private String taskDescription;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean taskStatus=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private AppUser user;
}

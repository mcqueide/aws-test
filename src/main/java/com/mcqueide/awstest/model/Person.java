package com.mcqueide.awstest.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "person_id_sequence"
    )
    @SequenceGenerator(
        name = "person_id_sequence",
        sequenceName = "person_id_sequence",
        allocationSize = 1
    )
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    public Person() {
    }

    public Person(Long id, String name, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}

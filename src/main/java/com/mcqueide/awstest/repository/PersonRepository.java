package com.mcqueide.awstest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcqueide.awstest.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
}

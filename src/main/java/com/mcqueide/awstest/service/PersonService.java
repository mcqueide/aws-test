package com.mcqueide.awstest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mcqueide.awstest.model.Person;
import com.mcqueide.awstest.repository.PersonRepository;

@Service
public class PersonService {
    
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAllPeople() {
        return personRepository.findAll();
    }

    public Person getPerson(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}

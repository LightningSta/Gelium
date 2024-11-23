package org.example.databaseservice.Repository;

import org.example.databaseservice.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findPersonByUsername(String username);
    Optional<Person> findPersonByNickname(String nickname);
}
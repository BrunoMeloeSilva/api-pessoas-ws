package com.github.brunomeloesilva.pessoas.domain.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.brunomeloesilva.pessoas.domain.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}

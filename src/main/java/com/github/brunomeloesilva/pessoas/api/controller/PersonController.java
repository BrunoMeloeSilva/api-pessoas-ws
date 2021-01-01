package com.github.brunomeloesilva.pessoas.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.brunomeloesilva.pessoas.domain.model.Person;
import com.github.brunomeloesilva.pessoas.domain.model.repository.PersonRepository;

@RestController
@RequestMapping("/people")
public class PersonController {
	
	@Autowired
	PersonRepository personRepository;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Person registerPerson(@RequestBody @Valid Person person) {
			return personRepository.save(person);
	}
	
	@GetMapping
	public List<Person> getAllPeople() {	
		return personRepository.findAll();
	}
}

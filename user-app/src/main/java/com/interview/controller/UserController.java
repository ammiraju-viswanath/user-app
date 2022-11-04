package com.interview.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.model.User;
import com.interview.repo.UserRepo;

@RestController
public class UserController {

	@Autowired
	UserRepo userService;

	@PostMapping("/users")
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
		final var userdb = userService.save(user);
		final var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(userdb.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable String id) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(
						String.format("User with ID as %s  not found", id)));
		userService.delete(userdb);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> retriveAllUser() {
		return ResponseEntity.ok(userService.findAll());
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<EntityModel<User>> retriveUserById(@PathVariable String id) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(
						String.format("User with ID as %s  not found", id)));

		final EntityModel<User>  model = EntityModel.of(userdb);

		final var newLinK =  WebMvcLinkBuilder.linkTo(
				WebMvcLinkBuilder.methodOn(this.getClass()).retriveAllUser());

		final var link = newLinK.withRel("Additional User info Link");
		model.add(link);

		return ResponseEntity.ok(model);
	}


	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id,
			@Valid  @RequestBody User user
			) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(
						String.format("User with ID as %s  not found", id)));
		user.setId(userdb.getId());
		userService.save(user);
		return ResponseEntity.accepted().build();
	}























}

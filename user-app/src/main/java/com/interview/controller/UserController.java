package com.interview.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

	@PostConstruct
	public void loadData() {

		final List<LocalDate> randomDates = LocalDate.of(1900, 1, 1).datesUntil(LocalDate.now()).limit(501)
				.collect(Collectors.toList());
		Collections.shuffle(randomDates);

		final var random = new Random();
		final List<User> users = IntStream.range(1, 501)
				.mapToObj(i -> User.builder().name("name" + (random.nextInt() % 2 == 0 ? 1 : 3))
						.birthdate(randomDates.get(i))
						.email("test" + (random.nextInt() % 3 == 0 ? 5 : 7) + "@gmail.com")
						.address("address" + +(random.nextInt() % 7 == 0 ? 2 : 4)).build())
				.collect(Collectors.toList());
		userService.saveAll(users);

	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> retriveAllUser() {
		return ResponseEntity.ok(userService.findAll());
	}

	@GetMapping("/v1/users")
	public ResponseEntity<Page<User>> retriveAllUserWithsearch(
			@RequestParam(required = false, defaultValue = "0") int pageNo,
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "id#desc") String[] sortAndOrder,
			@RequestParam(required = false, defaultValue = "") String searchCriteria) {

		//return ResponseEntity.ok(userService.findAll());
		final List<Order> orders = Arrays.stream(sortAndOrder).filter(s -> s.contains("#")).map(s -> s.split("#"))
				.map(arr -> new Order(Direction.fromString(arr[1]), arr[0])).collect(Collectors.toList());

		final Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(orders));

		final var data = searchCriteria.isBlank() ? userService.findAll(paging)
				: userService.findByKeyword(paging, searchCriteria);

		return ResponseEntity.ok(data);
	}



	@GetMapping("/users/{id}")
	public ResponseEntity<EntityModel<User>> retriveUserById(@PathVariable String id) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("User with ID as %s  not found", id)));

		final var newLinK = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retriveAllUser())
				.withRel("Additional User info Link");

		return ResponseEntity.ok(EntityModel.of(userdb, newLinK));
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id, @Valid @RequestBody User user) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("User with ID as %s  not found", id)));
		user.setId(userdb.getId());
		userService.save(user);
		return ResponseEntity.accepted().build();
	}

}

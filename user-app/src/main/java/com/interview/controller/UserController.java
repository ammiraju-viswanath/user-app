package com.interview.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.dto.Address;
import com.interview.dto.UserAddressWrapper;
import com.interview.model.User;
import com.interview.repo.UserRepo;

@RestController
public class UserController {

	@Autowired
	UserRepo userService;

	@Autowired
	RestTemplate template;

	@PostMapping("/users")
	public ResponseEntity<User> addUser(@Valid @RequestBody UserAddressWrapper userAddress) {
		final var userdb = userService.save(userAddress.getUser());

		final var userId = userdb.getId();
		userAddress.getAddress().stream().forEach(address->address.setUserid(userId) );
		final var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		userAddress.getAddress().stream().forEach(address-> template
				.exchange("http://ADDRESS-APP/addresses", HttpMethod.POST,
						new HttpEntity<>(address, headers), Address.class));


		final var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(userId).toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable String id) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(
						String.format("User with ID as %s  not found", id)));

		final var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final ResponseEntity<List<Address>> addresses = template
				.exchange("http://ADDRESS-APP/addresses/users/"+ userdb.getId() , HttpMethod.GET,
						new HttpEntity<>(null, headers), new ParameterizedTypeReference<List<Address>>() {});

		addresses.getBody().forEach(i->
		template
		.exchange("http://ADDRESS-APP/addresses/"+ i.getId() , HttpMethod.DELETE,
				new HttpEntity<>(null, headers), Object.class));




		userService.delete(userdb);
		return ResponseEntity.noContent().build();
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
	public ResponseEntity<EntityModel<UserAddressWrapper>> retriveUserById(@PathVariable String id) {
		final var userdb = userService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("User with ID as %s  not found", id)));



		final var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final ResponseEntity<List<Address>> addresses = template
				.exchange("http://ADDRESS-APP/addresses/users/"+ userdb.getId() , HttpMethod.GET,
						new HttpEntity<>(null, headers), new ParameterizedTypeReference<List<Address>>() {});
		final var reply=

				UserAddressWrapper.builder().address(addresses.getBody()).user(userdb).build();



		final var newLinK = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retriveAllUser())
				.withRel("Additional User info Link");

		return ResponseEntity.ok(EntityModel.of(reply, newLinK));
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

package com.interview;



import java.net.URI;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.interview.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserAppApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserApiTest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTempWithAdmin = new  TestRestTemplate()
			.withBasicAuth("springer", "secret");

	private final TestRestTemplate restTempWithUser = new  TestRestTemplate()
			.withBasicAuth("spring", "secret");


	private final HttpHeaders headers = new HttpHeaders();
	private final User user = User.builder().build();

	public URI loaduri(String uri) {
		return URI.create(String.format("http://localhost:%s%s", port, uri));
	}

	@BeforeEach
	public void loadUser() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		user.setName("UserName");
		user.setBirthdate(LocalDate.of(1900, 11, 12));
		user.setEmail("Test@gmail.com");
		user.setAddress("address details");

	}

	@Test
	@Order(1)
	public void whenValidUser_create() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange
				(loaduri("/users"), HttpMethod.POST, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.CREATED.value(), reply.getStatusCode().value());
		Assert.assertNotNull(reply.getHeaders().getLocation());


		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange
				(loaduri("/users"), HttpMethod.POST, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());


	}

	@Test
	@Order(5)
	public void whenValidUser_deleteUsers() {

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange
				(loaduri("/users/1"), HttpMethod.DELETE, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange
				(loaduri("/users/1"), HttpMethod.DELETE, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(3)
	public void whenValidUser_retriveAllUsers() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange
				(loaduri("/users"), HttpMethod.GET, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange
				(loaduri("/users"), HttpMethod.GET, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), replyWithUser.getStatusCode().value());



	}

	@Test
	@Order(2)
	public void whenValidUser_retriveOneUsers() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange
				(loaduri("/users/1"), HttpMethod.GET, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange
				(loaduri("/users/1"), HttpMethod.GET, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(4)
	public void whenValidUser_updateUsers() {
		user.setName("updatedName");
		user.setAddress("address details updated");

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange
				(loaduri("/users/1"), HttpMethod.PUT, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange
				(loaduri("/users/1"), HttpMethod.PUT, new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());

	}
}

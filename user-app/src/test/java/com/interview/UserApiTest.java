package com.interview;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

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

import com.interview.model.Address;
import com.interview.model.Name;
import com.interview.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserAppApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserApiTest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTempWithAdmin = new TestRestTemplate().withBasicAuth("springer", "secret");

	private final TestRestTemplate restTempWithUser = new TestRestTemplate().withBasicAuth("spring", "secret");

	private final HttpHeaders headers = new HttpHeaders();
	private final User user = User.builder().build();
	private final Name name = Name.builder().firstName("firstName").secondName("secondName").build();
	private final Address address1 = Address.builder().line1("address1-1").line2("address2-1").line3("address3-1")
			.line4("address4-1").postcode("postcode-1").build();
	private final Address address2 = Address.builder().line1("address2-1").line2("address2-2").line3("address2-3")
			.line4("address2-4").postcode("postcode2").build();

	public URI loaduri(String uri) {
		return URI.create(String.format("http://localhost:%s%s", port, uri));
	}

	@BeforeEach
	public void loadUser() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		user.setName(name);
		user.setBirthdate(LocalDate.of(1900, 11, 12));
		user.setEmail("Test@gmail.com");
		user.setAddress(List.of(address1, address2));

	}

	@Test
	@Order(14)
	public void when_invalid_data_format_shouldFail() {
		user.setEmail("asdsadsadsad");
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users"), HttpMethod.POST,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), reply.getStatusCode().value());

	}

	@Test
	@Order(13)
	public void when_invalid_format_shouldFail() {

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users/as"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), reply.getStatusCode().value());

	}



	@Test
	@Order(12)
	public void when_invalid_Id_shouldFail() {

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users/10000"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		final var id = "10000";
		final var expected =String.format("User with ID as %s  not found", id);

		Assert.assertTrue(reply.getBody().toString().contains(expected));


	}




	@Test
	@Order(15)
	public void when_invalid_method_call_shouldFail() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users"), HttpMethod.DELETE,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), reply.getStatusCode().value());


	}

	@Test
	@Order(11)
	public void when_invalid_User_shouldFail() {
		final ResponseEntity<Object> reply = new TestRestTemplate().withBasicAuth("springer1232", "secret").exchange(loaduri("/users"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), reply.getStatusCode().value());

	}



	@Test
	@Order(1)
	public void whenValidUser_create() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users"), HttpMethod.POST,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.CREATED.value(), reply.getStatusCode().value());
		Assert.assertNotNull(reply.getHeaders().getLocation());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/users"), HttpMethod.POST,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(5)
	public void whenValidUser_deleteUsers() {

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users/1"), HttpMethod.DELETE,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/users/1"), HttpMethod.DELETE,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(3)
	public void whenValidUser_retriveAllUsers() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/users"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(6)
	public void whenValidUser_retriveAllUsersWithFilterAndVersion() {
		// String url = String.format("http://localhost:%s%s", port, "/v1/users");
		// URI uri =UriComponentsBuilder.fromHttpUrl(url).queryParam("",
		// "").queryParam("", "").build().toUri();

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/v1/users"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/v1/users"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(2)
	public void whenValidUser_retriveOneUsers() {
		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users/1"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/users/1"), HttpMethod.GET,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), replyWithUser.getStatusCode().value());

	}

	@Test
	@Order(4)
	public void whenValidUser_updateUsers() {
		name.setFirstName("updated-firstName");
		name.setSecondName("updated-Second");
		user.setName(name);
		user.setAddress(List.of(address1, address2));

		final ResponseEntity<Object> reply = restTempWithAdmin.exchange(loaduri("/users/1"), HttpMethod.PUT,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), reply.getStatusCode().value());

		final ResponseEntity<Object> replyWithUser = restTempWithUser.exchange(loaduri("/users/1"), HttpMethod.PUT,
				new HttpEntity<>(user, headers), Object.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN.value(), replyWithUser.getStatusCode().value());

	}
}

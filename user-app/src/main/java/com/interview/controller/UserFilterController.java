package com.interview.controller;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonFilter("dynamicFilter") // MJV
@JsonIgnoreProperties(value = { "gender", "email" }, allowGetters = true)
class UserFilter {
	private String firstname;
	private String LastName;
	private String gender;
	private String address;
	private String phone;
	private String email;
	@JsonIgnore()
	private String password;
}

@RestController
public class UserFilterController {

	@GetMapping("/v2/userfilter")
	public MappingJacksonValue UserDynamicFilter() {
		final var mybean = new UserFilter("firstnameValue", "LastNameValue", "genderValue", "addressValue",
				"phoneValue", "emailValue", "passwordValue");

		final var mjv = new MappingJacksonValue(mybean);

		final FilterProvider filter = new SimpleFilterProvider().addFilter("dynamicFilter",
				SimpleBeanPropertyFilter.filterOutAllExcept("firstname", "phone"));
		mjv.setFilters(filter);

		return mjv;

	}

	@GetMapping("/v1/userfilter")
	public UserFilter UserStaticFilter() {
		return new UserFilter("firstnameValue", "LastNameValue", "genderValue", "addressValue", "phoneValue",
				"emailValue", "passwordValue");
	}
}

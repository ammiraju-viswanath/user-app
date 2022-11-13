package com.interview.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserVersionController{

	@GetMapping(value="/person/headers",headers="API-VERSION-1")
	public UserV1 getUserV1Headers() {
		return populateUserV1();
	}

	@GetMapping(value="/person/params",params="verson=1")
	public UserV1 getUserV1Params(@RequestParam String verson) {
		return populateUserV1();
	}

	@GetMapping(value="/person/produces",produces="application/vnd.api-v1+xml")
	public UserV1 getUserV1Produces() {
		return populateUserV1();
	}
	@GetMapping("/v1/person")
	public UserV1 getUserV1Uri() {
		return populateUserV1();
	}

	@GetMapping(value="/person/headers",headers="API-VERSION-2")
	public UserV2 getUserV2Headers() {
		return populateUserV2();
	}

	@GetMapping(value="/person/params",params="verson=2")
	public UserV2 getUserV2Params(@RequestParam String verson) {
		return populateUserV2();
	}


	@GetMapping(value="/person/produces",produces="application/vnd.api-v2+json")
	public UserV2 getUserV2Produces() {
		return populateUserV2();
	}

	@GetMapping("/v2/person")
	public UserV2 getUserV2Uri() {
		return populateUserV2();
	}

	public UserV1 populateUserV1() {

		return new UserV1("Viswanath Ammiraju");
	}

	public UserV2 populateUserV2() {
		return new UserV2("Viswanath",  "Ammiraju");
	}

}
record UserV1(String name) {}
record UserV2(String firstName,	String secondName) {}

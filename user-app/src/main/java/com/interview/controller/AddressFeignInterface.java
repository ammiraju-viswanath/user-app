package com.interview.controller;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.interview.dto.Address;


@FeignClient("address-app")
public interface AddressFeignInterface {

	@PostMapping("/addresses")
	ResponseEntity<Address> addAddress(Address address);

	@DeleteMapping("/addresses/{id}")
	ResponseEntity<Address> deleteAddress(@PathVariable(name="id") String id);

	@GetMapping("/addresses/users/{id}")
	ResponseEntity<List<Address>> retriveAllAddressByUserId(@PathVariable(name="id") String id);

}
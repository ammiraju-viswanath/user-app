package com.interview.dto;

import java.util.List;

import javax.validation.Valid;

import com.interview.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder@NoArgsConstructor @AllArgsConstructor
public class UserAddressWrapper {
	private @Valid User user;
	private List<Address> address;

}

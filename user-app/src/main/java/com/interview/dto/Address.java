package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder@NoArgsConstructor @AllArgsConstructor
public class Address {

	private Integer id;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private String postcode;
	private Integer userid;


}

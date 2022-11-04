package com.interview.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name="user_table")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@NotBlank
	@Size(min = 3, max = 50, message = "name is required with atleast 3 char.")
	private String name;

	@PastOrPresent
	private LocalDate birthdate;
	@Email
	private String email;
	@NotBlank
	private String address;

}

package com.interview.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="address_table")
@Data
@Builder@NoArgsConstructor @AllArgsConstructor
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private String postcode;

	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
			fetch = FetchType.EAGER, mappedBy = "address")
	@JsonIgnore
	private List<User> user;


}

package com.example.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="COUNTRY_MASTER")
public class Country {
	@Id
	private Integer countryId;
	private String countryName;

}

package com.example.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "STATE_MASTER")
public class State {
	@Id
	private Integer stateId;
	private String stateName;
	private Integer countryId;

}

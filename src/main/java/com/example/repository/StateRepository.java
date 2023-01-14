package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.State;

public interface StateRepository extends JpaRepository<State, Serializable> {
	//select * from states_master where country_Id = ?
	public List<State> findByCountryId(Integer countryId);

}

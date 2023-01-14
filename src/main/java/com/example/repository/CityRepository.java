package com.example.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.City;

public interface CityRepository extends JpaRepository<City, Serializable> {
	//select * from City_Master where stateId=?
	public List<City> findByStateId(Integer stateId);

}

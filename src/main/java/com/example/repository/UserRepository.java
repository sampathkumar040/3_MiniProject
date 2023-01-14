package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.User;

public interface UserRepository extends JpaRepository<User, Serializable>{
	//select * from user_master where email=? 
	public User findByEmail(String email);
	
	//select * from user_master where email-? and user_pwd=?
	public User findByEmailAndUserPwd(String email, String pwd);

}

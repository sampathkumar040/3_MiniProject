package com.example.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.User;

public interface UserRepository extends JpaRepository<User, Serializable>{
	//select * from user_master where email=? 
	public User findByUserEmail(String userEmail);
	
	//select * from user_master where email-? and user_pwd=?
	public User findByUserEmailAndUserPwd(String userEmail, String userPwd);

}

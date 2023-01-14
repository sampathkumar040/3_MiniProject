package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.binding.LoginForm;
import com.example.binding.UnlockAccountForm;
import com.example.binding.UserForm;
import com.example.service.UserMngtService;

@RestController
public class UserMngtController {
	@Autowired
	private UserMngtService userMngtService;
	
	@GetMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginForm loginForm){
		String status = userMngtService.login(loginForm);
		return new ResponseEntity<>(status,HttpStatus.OK);
	}
	
	@GetMapping("/countries")
	public Map<Integer, String> loadCountries(){
		return userMngtService.getCountries();
	}
	@GetMapping("/states/{countryId}")
	public Map<Integer, String> loadStates(@PathVariable Integer countryId){
		return userMngtService.getStates(countryId);
	}
	@GetMapping("/email/{email}")
	public String emailCheck(@PathVariable String email) {
		return userMngtService.checkEmail(email);
	}
	
	@GetMapping("/cities/{stateId}")
	public Map<Integer, String> loadCities(@PathVariable Integer stateId){
		return userMngtService.getStates(stateId);
	}
	@PostMapping("/user")
	public ResponseEntity<String> userRegistration(@RequestBody UserForm userForm){
		String status = userMngtService.registerUser(userForm);
		return new ResponseEntity<>(status,HttpStatus.CREATED);
		
	}
	@PostMapping("/unlock")
	public ResponseEntity<String> unlockAccount(@RequestBody UnlockAccountForm unlockAccForm){
		String status = userMngtService.unlockAccount(unlockAccForm);
		return new ResponseEntity<>(status,HttpStatus.OK);
				
	}
	@GetMapping("/forgotPwd/{email}")
	public ResponseEntity<String> forgotPwd(@PathVariable String email){
		String status = userMngtService.forgotPassword(email);
		return new ResponseEntity<>(status, HttpStatus.OK);
		
	}
	
	
	
	
	
}

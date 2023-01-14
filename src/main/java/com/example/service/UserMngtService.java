package com.example.service;

import java.util.Map;

import com.example.binding.LoginForm;
import com.example.binding.UnlockAccountForm;
import com.example.binding.UserForm;

public interface UserMngtService {
	
	public String checkEmail(String email);
	
	public Map<Integer, String> getCountries();
	
	public Map<Integer, String> getStates(Integer countryId);
	
	public Map<Integer, String> getCities(Integer stateId);
	
	public String registerUser(UserForm userForm);
	
	public String unlockAccount(UnlockAccountForm accForm);
	
	public String login(LoginForm loginform);
	
	public String forgotPassword(String email);
	
	

}

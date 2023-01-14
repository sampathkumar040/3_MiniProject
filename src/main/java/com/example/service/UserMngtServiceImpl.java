package com.example.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.binding.LoginForm;
import com.example.binding.UnlockAccountForm;
import com.example.binding.UserForm;
import com.example.entity.City;
import com.example.entity.Country;
import com.example.entity.State;
import com.example.entity.User;
import com.example.repository.CityRepository;
import com.example.repository.CountryRepository;
import com.example.repository.StateRepository;
import com.example.repository.UserRepository;
import com.example.util.EmailUtils;

@Service
public class UserMngtServiceImpl implements UserMngtService{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private EmailUtils emailUtils;
	
	@Override
	public String checkEmail(String email) {
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			return "UNIQUE";			
		}		
		return "DUPLICATE";
	}

	@Override
	public Map<Integer, String> getCountries() {
		List<Country> countries = countryRepository.findAll();
		
		Map<Integer, String> countryMap=new HashMap<>();		
		countries.forEach(country->{
			countryMap.put(country.getCountryId(), country.getCountryName());
		});
		return countryMap;
	}

	@Override
	public Map<Integer, String> getStates(Integer countryId) {
		List<State> states=stateRepository.findByCountryId(countryId);
		Map<Integer, String> stateMap=new HashMap<>();
		states.forEach(state->{
			stateMap.put(state.getStateId(), state.getStateName());
			
		});
		return stateMap;
	}

	@Override
	public Map<Integer, String> getCities(Integer stateId) {
		List<City> cities=cityRepository.findByStateId(stateId);
		
		Map<Integer, String> citiesMap=new HashMap<>();
		cities.forEach(city->{
			citiesMap.put(city.getCityId(), city.getCityName());
		});
		return citiesMap;
	}

	public String registerUser(UserForm userForm) {
		//copy data from binding to entity object
		User entity =new User();
		BeanUtils.copyProperties(userForm, entity);
		
		//generate and set random  pwd 
		entity.setUserPwd(generateRandomPwd());
		
		
		//Set Account status locked
		entity.setAccStatus("LOCKED");
		userRepository.save(entity);
		
		//send email to unlock Account
		String to=userForm.getEmail();
		String subject="Registration Email";
		String body=readEmailBody("REG_EMAIL_BODY.txt", entity);
		emailUtils.sendMail(to, subject, body);
		
		return "User Account Created ";
	}

	public String unlockAccount(UnlockAccountForm accForm) {
		String email = accForm.getEmail();
		User user = userRepository.findByEmail(email);
		if(user!=null && user.getUserPwd().equals(accForm.getTempPwd())) {
		user.setUserPwd(accForm.getNewPwd());
		user.setAccStatus("UNLOCKED");
		userRepository.save(user);
		
		return "Account Unlocked";
	}
	return "Invalid Temparory Password";
	}

	public String login(LoginForm loginform) {
		User user = userRepository.findByEmailAndUserPwd(loginform.getEmail(), loginform.getPwd());
		if(user==null) {
			return "Invalid Credentials";
		}
		
		if(user.getAccStatus().equals("LOCKED")) {
			return "ACCOUNT LOCKED";
		}
		
		return "SUCCESS";
	}

	@Override
	public String forgotPassword(String email) {
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			return "No Account Found";
		}
		//Todo: send email to user  with pwd
		String subject="Recover Password ";
		String body=readEmailBody("FORGOT_PWD_EMAIL_BODY.txt", user);
		emailUtils.sendMail(email, subject, body);
		return "Password sent to register mail ";
	}
	
	
	public String generateRandomPwd() {
		 
		String text="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		StringBuilder sb=new StringBuilder();
		Random random=new Random();
		int pwdLength=6;
		for (int i = 1; i < pwdLength; i++) {
			int index = random.nextInt(text.length());
			sb.append(text.charAt(index));			
		}
		return sb.toString();
	}
	
	public String readEmailBody(String fileName,User user)  {
		
		StringBuffer sb=new StringBuffer();
		
		try(Stream<String> lines=Files.lines(Paths.get(fileName))){
			lines.forEach(line->{
				line=line.replace("{FNAME}", user.getFname());
				line=line.replace("{LNAME}", user.getLname());
				line=line.replace("{TEMP_PWD}", user.getUserPwd());
				line=line.replace("{EMAIL}", user.getEmail());
				line=line.replace("{PWD}", user.getUserPwd());
				sb.append(line);
				
			});			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}


}

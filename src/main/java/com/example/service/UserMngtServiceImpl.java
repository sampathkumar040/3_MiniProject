package com.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.example.constants.AppConstatnts;
import com.example.entity.City;
import com.example.entity.Country;
import com.example.entity.State;
import com.example.entity.User;
import com.example.exception.RegAppException;
import com.example.pros.AppProperties;
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
	
	@Autowired
	private AppProperties appPro;
	
	@Override
	public boolean checkEmail(String email) {
		User user = userRepository.findByUserEmail(email);
		
		if(user != null ) {
			return false;			
		}else {
			return true;
		}
		
	}

	@Override
	public Map<Integer, String> getCountries() {
		List<Country> findAll = countryRepository.findAll();
		
		Map<Integer, String> countryMap=new HashMap<>();
		for(Country entity: findAll) {
			countryMap.put(entity.getCountryId(), entity.getCountryName());
		}
		
		
		/*countries.forEach(country->
			countryMap.put(country.getCountryId(), country.getCountryName())
		);*/
		return countryMap;
	}

	@Override
	public Map<Integer, String> getStates(Integer countryId) {
		List<State> states=stateRepository.findByCountryId(countryId);
		Map<Integer, String> stateMap=new HashMap<>();
		for(State state: states) {
			stateMap.put(state.getCountryId(), state.getStateName());
		}
		
		
		/*states.forEach(state->
			stateMap.put(state.getStateId(), state.getStateName())		
		);*/
		return stateMap;
	}

	@Override
	public Map<Integer, String> getCities(Integer stateId) {
		List<City> cities=cityRepository.findByStateId(stateId);
		
		Map<Integer, String> citiesMap=new HashMap<>();
		for(City city: cities) {
			citiesMap.put(city.getCityId(), city.getCityName());
		}
		/*cities.forEach(city->
			citiesMap.put(city.getCityId(), city.getCityName())
		);*/
		return citiesMap;
	}

	public boolean registerUser(UserForm userForm) {
		
		//Set Account status locked
		userForm.setUserAccStatus(AppConstatnts.DUPLICATE);
		
		//generate and set random  pwd 
		userForm.setUserPwd(generateRandomPwd());
				
				
		//copy data from binding to entity object
		User entity =new User();
		BeanUtils.copyProperties(userForm, entity);
		
		
		
		
		
		User save = userRepository.save(entity);
		
		if(null !=save.getUserId()) {
			return sendRegEmail(userForm);
		}
		
		
	/*	
		//send email to unlock Account
		String to=userForm.getUserEmail();
		String subject="Registration Email";
		String body=readEmailBody("REG_EMAIL_BODY.txt", entity);
		//emailUtils.sendMail(to, subject, body);
		*/
		return false;
	}

	public String unlockAccount(UnlockAccountForm accForm) {
		String email = accForm.getEmail();
		User user = userRepository.findByUserEmail(email);
		if(user!=null && user.getUserPwd().equals(accForm.getTempPwd())) {
		user.setUserPwd(accForm.getNewPwd());
		user.setUserAccStatus(AppConstatnts.LOCKED);
		userRepository.save(user);
		
		return AppConstatnts.ACCOUNT_UNLOCKED;
	}
	return AppConstatnts.INVALID_TEMPARORY_PASSWORD;
	}

	public String login(LoginForm loginform) {
		User user = userRepository.findByUserEmailAndUserPwd(loginform.getEmail(), loginform.getPwd());
		if(user==null) {
			return AppConstatnts.INVALID_CREDENTIALS;
		}
		
		if(user.getUserAccStatus().equals(AppConstatnts.LOCKED)) {
			return AppConstatnts.ACCOUNT_LOCKED;
		}
		
		return AppConstatnts.ACCOUNT_UNLOCKED;
	}

	public boolean sendRegEmail(UserForm userForm) {
		boolean emailSent=false;
		try {
			Map<String, String> messages=appPro.getMessages();
			String subject = messages.get(AppConstatnts.REG_MAIL_SUBJECT);
			String bodyFileName = messages.get(AppConstatnts.REG_MAIL_BODY);
			String body=readEmailBody(bodyFileName, userForm);
			emailUtils.sendMail(subject, body, userForm.getUserEmail());
			emailSent=true;		
		}catch (Exception e) {
			throw new RegAppException(e.getMessage());
		}
		
		return emailSent;
		
		
		/*User user = userRepository.findByEmail(email);
		
		if(user==null) {
			return "No Account Found";
		}
		String subject="Recover Password ";
		String body=readEmailBody("FORGOT_PWD_EMAIL_BODY.txt", user);
		emailUtils.sendMail(email, subject, body);
		return "Password sent to register mail ";
	}*/
	}
	
	
	public String generateRandomPwd() {
		
		String tempPwd=null;
		int leftLimit=48; // number  = 0;
		int rightLimit=122; //letter = 'z'
		int targetStringLength=6;
		
		Random random=new Random();
		
		tempPwd=random.ints(leftLimit,rightLimit+1).filter(i->(i<=57 || i>=65)&&(i<=90 || i>=97))
				.limit(targetStringLength).collect(StringBuilder:: new,StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return tempPwd;
		
		/* 
		String text="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		StringBuilder sb=new StringBuilder();
		Random random=new Random();
		int pwdLength=6;
		for (int i = 1; i < pwdLength; i++) {
			int index = random.nextInt(text.length());
			sb.append(text.charAt(index));			
		}
		return sb.toString();*/
		
		
		
	}
	
	public String readEmailBody(String fileName,UserForm userForm)  {
		String mailBody=null;
		StringBuilder buffer=new StringBuilder();
		Path path=Paths.get(fileName);
		try(Stream<String> stream=Files.lines(path)){
			stream.forEach(line->{
				buffer.append(line);
			});
			
			mailBody=buffer.toString();
			mailBody=mailBody.replace(AppConstatnts.FNAME, userForm.getUserFname());
			mailBody=mailBody.replace(AppConstatnts.EMAIL, userForm.getUserEmail());
			mailBody=mailBody.replace(AppConstatnts.TEMP_PWD, userForm.getUserPwd());
			mailBody=mailBody.replace(AppConstatnts.PWD, userForm.getUserPwd());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return mailBody;
		
		
		
		/*StringBuilder sb=new StringBuilder();
		
		try(Stream<String> lines=Files.lines(Paths.get(fileName))){
			lines.forEach(line->{
				line=line.replace("{FNAME}", userForm.getUserFname());
				line=line.replace("{LNAME}", userForm.getUserLname());
				line=line.replace("{TEMP_PWD}", userForm.getUserPwd());
				line=line.replace("{EMAIL}", userForm.getUserEmail());
				line=line.replace("{PWD}", userForm.getUserPwd());
				sb.append(line);
				
			});	*/		
	
	}




}

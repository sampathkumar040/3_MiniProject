package com.example.binding;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserForm {
	

	private Integer userId;
	private String userFname;
	private String userLname;
	private String userEmail;
	private Integer userPhno;
	private LocalDate userDob;
	private String userGender;
	private Integer userCountry;
	private Integer userState;
	private Integer userCity;
	private String userPwd;
	private String userAccStatus;
	private LocalDate createdDate;
	private LocalDate updatedDate;
}
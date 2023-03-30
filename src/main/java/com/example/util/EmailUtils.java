package com.example.util;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.exception.RegAppException;
@Component
@Configuration
public class EmailUtils {
	@Autowired
	private JavaMailSender mailSender;

	public boolean sendMail(String to,String subject,String body) {
		boolean isSent=false;
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try{
		
			MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body,true);

			mailSender.send(messageHelper.getMimeMessage());
			
			isSent=true;

		}catch (Exception e) {
			throw new RegAppException();
		}
		return isSent;

	}


}

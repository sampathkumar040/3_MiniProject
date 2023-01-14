package com.example.util;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
@Component
public class EmailUtils {
	@org.springframework.beans.factory.annotation.Autowired(required=true)
	private JavaMailSender mailSender;

	public boolean sendMail(String to,String subject,String body) {
		boolean isSent=false;

		try{
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body,true);

			mailSender.send(mimeMessage);
			isSent=true;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return isSent;

	}


}

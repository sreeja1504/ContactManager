package com.sreeja.service;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import com.sreeja.entities.EmailRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@EnableScheduling
public class EmailService {

	boolean flag=false;
	@Autowired
	private EmailRequest request;
	
	public void myFun(EmailRequest request) {
		this.flag=true;
		this.request=request;
	}
	
	@Scheduled(cron="*/10 * * * * *")
	public  void sendEmail() {
        if(!flag) {
        	System.out.println("give valid credentials...!");
        	return;
        }
		String from = "madiresrija15@gmail.com";
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);
		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		// 1 get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("madiresrija15@gmail.com", "123&123");
			}
		});
		session.setDebug(true);
		MimeMessage m = new MimeMessage(session);
		try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(request.getTo()));
			m.setSubject(request.getSubject());
			m.setText(request.getMessage());
			// 3 send message using transport class
			Transport.send(m);
			System.out.println("Sent success............");
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public boolean sendAttach(String subject, String message, String to) {
		boolean f = false;
		String from = "madiresrija15@gmail.com";
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);
		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		// 1 get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("madiresrija15@gmail.com", "123&123");
			}
		});
		session.setDebug(true);
		MimeMessage m = new MimeMessage(session);
		try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			// sending attachment
			String path = "C:\\Users\\Praveen\\Pictures\\Saved Pictures\\flower1.jpg";
			MimeMultipart multipart = new MimeMultipart();
			// for text
			MimeBodyPart textMime = new MimeBodyPart();
			MimeBodyPart fileMime = new MimeBodyPart();
			try {
				textMime.setText(message);
				File file = new File(path);
				fileMime.attachFile(file);
				multipart.addBodyPart(textMime);
				multipart.addBodyPart(fileMime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			m.setContent(multipart);

			// 3 send message using transport class
			Transport.send(m);
			System.out.println("Sent success............");
			f = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;

	}

}

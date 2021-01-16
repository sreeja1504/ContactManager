package com.sreeja.controller;
import org.springframework.web.bind.annotation.RestController;
import com.sreeja.entities.EmailRequest;
import com.sreeja.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private EmailService emailService;
	
	@RequestMapping(value="/sendemail",method=RequestMethod.POST)
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request){
		
        try {
        	emailService.myFun(request);
            return ResponseEntity.ok("Email is sent successfully..");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email not sent..");
        }
    }


	
	@RequestMapping(value="/sendAttachment",method=RequestMethod.POST)
	public ResponseEntity<?> sendAttachment(@RequestBody EmailRequest request){
		boolean result = emailService.sendAttach(request.getSubject(),request.getMessage(),request.getTo());
		if(result)
		return ResponseEntity.ok("Email is sent successfully..");
		else
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email not sent..");

	}

}

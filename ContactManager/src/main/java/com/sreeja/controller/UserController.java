package com.sreeja.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sreeja.dao.ContactRepository;
import com.sreeja.dao.UserRepository;
import com.sreeja.entities.Contact;
import com.sreeja.entities.User;
import com.sreeja.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model m,Principal principal) {
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		m.addAttribute("user",user);

	}
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
//		String userName=principal.getName();
//		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	@GetMapping("/add-contact")
   public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
	   return "normal/add_contact_form";
   }
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal
			, HttpSession session) {
		try {
		String name=principal.getName();
		User user=userRepository.getUserByUserName(name);
		
		//processing and uploading file
		if(file.isEmpty()) {
			 System.out.println("File is empty");
			 contact.setImage("contact.png");
		}else {
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
			 System.out.println("Image is uploaded");

		}
		contact.setUser(user); 
		user.getContacts().add(contact);
		
        userRepository.save(user);
		System.out.println("DATA"+contact);
		session.setAttribute("message",new Message("Your contact is added || Add more..","success"));
		}catch(Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong try again ","danger"));

		}
		return "normal/add_contact_form";

	}
	//show contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m,Principal principal) {
		m.addAttribute("title","show User contacts ");
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts =contactRepository.findContactsByUser(user.getId(),pageable);
        m.addAttribute("contacts",contacts);
        m.addAttribute("currentPage",page);
        m.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contact"; 
	}
	//showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal ) {
		Optional<Contact> contactOptional =  contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		
		return "normal/contact_detail";
	}
	//delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId,Model model,HttpSession session,Principal principal) {
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		//contact.setUser(null);
		//check assignment that unkonwn contact to be deleted from url
		//contactRepository.delete(contact);
		User user = userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);

		session.setAttribute("message", new Message("Contact deleted succesfully...","success"));
		return "redirect:/user/show-contacts/0";
	}
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title","Update Contact");
		Contact contact = contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal) {
       try {
    	 Contact oldcontactDetail = contactRepository.findById(contact.getcId()).get();
    	   if(!file.isEmpty()) {
    		   //rewrite file 
    		   //delete old pic
      			File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile,oldcontactDetail.getImage());
                file1.delete();
    		   //update new photo

   			File saveFile = new ClassPathResource("static/img").getFile();
   			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
 		   contact.setImage(file.getOriginalFilename());

    		   
    	   }
    	   else {
    		   contact.setImage(oldcontactDetail.getImage());
    	   }
    	   User user=userRepository.getUserByUserName(principal.getName());
    	   contact.setUser(user);
    	   contactRepository.save(contact);
    	   session.setAttribute("message", new Message("Your contact is updated...","success"));
       }catch(Exception e) {
    	   e.printStackTrace();
       }
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title","Profile Page");
		return "normal/profile";
		
	}
}

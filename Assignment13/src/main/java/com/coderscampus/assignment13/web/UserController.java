package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String getCreateUser (ModelMap model) {
		
		model.put("user", new User());
		
		return "register";
	}
	

	
	@PostMapping("/register")
	public String postCreateUser (User user) {
		System.out.println(user);
		userService.saveUser(user);
		return "redirect:/register";
	}
	
	@GetMapping("/users")
	public String getAllUsers (ModelMap model) {
		Set<User> users = userService.findAll();
		
		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}
		
		return "users";
	}
	
	@GetMapping("/users/{userId}")
	public String getOneUser (ModelMap model, @PathVariable Long userId) {
	    User user = userService.findById(userId);
	    
	    if (user.getAddress() == null) {
	        user.setAddress(new Address());
	    }
	    
	    model.addAttribute("user", user);
	    model.put("users", Arrays.asList(user));
	    return "userDetails";  
	}

	
	@PostMapping("/users/{userId}")
	public String postOneUser (User user) {
		userService.saveUser(user);
		return "redirect:/users/"+user.getUserId();
	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
	

	@PostMapping("/users/{userId}/accounts")
	public String createOrUpdateAccount(@PathVariable Long userId, @ModelAttribute Account account) {
	    Account savedAccount;
	    if (account.getAccountId() == null) {
	        savedAccount = userService.createAccountForUser(userId);
	    } else {
	        savedAccount = userService.saveAccount(account);
	    }
	    return "redirect:/users/" + userId + "/accounts/" + savedAccount.getAccountId();
	}

	
	@GetMapping("/users/{userId}/accounts/{accountId}/details")
	public String showAccountDetails(@PathVariable Long userId, @PathVariable Long accountId, Model model) {
		Account account = userService.findByAccountId(accountId);
		model.addAttribute("account", account);
		model.addAttribute("userId", userId);
		return "accountDetails";
	}
	
	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
	    Account account = userService.findByAccountId(accountId);
	    
	    if (account == null) {
	        account = new Account();
	    }
	    
	    model.addAttribute("account", account);
	    model.addAttribute("userId", userId);
	    
	    return "account";
	}


	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postOneAccount(@PathVariable Long userId, @PathVariable Long accountId, Account account) {
		account.setAccountId(accountId);
		userService.saveAccount(account);
		return "redirect:/users/" + userId + "/accounts/" + accountId;
	}

}
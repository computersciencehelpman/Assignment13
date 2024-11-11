package com.coderscampus.assignment13.web;

import java.util.ArrayList;
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
	
	@GetMapping("/users/{userId}/accounts/new")
	public String createAccountForm(@PathVariable Long userId, Model model) {
	    Account newAccount = userService.createAccountForUser(userId);
	    model.addAttribute("userId", userId);
	    model.addAttribute("account", newAccount);
	    return "account"; 
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
	
	@PostMapping("/users/{userId}/update")
	public String updateUser(@PathVariable Long userId, @ModelAttribute User user) {
	    User existingUser = userService.findById(userId);
	    existingUser.setUsername(user.getUsername());
	    existingUser.setName(user.getName());
	    userService.saveUser(existingUser);
	    System.out.println("Updating user "+ userId);
	    return "redirect:/users";
	}


	
	@GetMapping("/users/{userId}")
	public String getOneUser(ModelMap model, @PathVariable Long userId) {
	    User user = userService.findById(userId);
	    
	    if (user.getAddress() == null) {
	        user.setAddress(new Address());
	    }
	    
	    
	    if (user.getAccounts() == null) {
	        user.setAccounts(new ArrayList<>());
	    }
	    
	    model.addAttribute("user", user);
	    model.addAttribute("accounts", user.getAccounts());
	    System.out.println("Confirm");
	    return "userDetails";  
	}

	
	@PostMapping("/users/{userId}")
	public String postOneUser (@PathVariable Long userId, @ModelAttribute User user) {
		user.setUserId(userId); 
		userService.saveUser(user);
		return "redirect:/users/";
	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
	
	@PostMapping("/users/{userId}/accounts")
	public String createOrUpdateAccount(@PathVariable Long userId, @ModelAttribute Account account) {
	    User user = userService.findById(userId);
	    System.out.println("User fetched: " + user);
	    Account savedAccount;

	    if (account.getAccountId() == null) { 
	        savedAccount = userService.createAccountForUser(userId);
	        savedAccount.setAccountName(account.getAccountName());
	        userService.saveAccount(savedAccount);
	    } else { 
	        savedAccount = userService.saveAccount(account);
	    }

	    
	    if (!user.getAccounts().contains(savedAccount)) {
	        user.getAccounts().add(savedAccount);
	        userService.saveUser(user); 
	    }

	    return "redirect:/users/" + userId;
	}

	
	@GetMapping("/users/{userId}/accounts/{accountId}/details")
	public String showAccountDetails(@PathVariable Long userId, @PathVariable Long accountId, Model model) {
		System.out.println("userId: " + userId);
	    System.out.println("accountId: " + accountId);
		Account account = userService.findByAccountId(accountId);
		model.addAttribute("account", account);
		model.addAttribute("userId", userId);
		System.out.println("Account id:" +accountId);
		return "account";
	}
	
//	@PostMapping("/users/{userId}/accounts/{accountId}/save")
//	public String saveAccount(@PathVariable Long userId, @PathVariable Long accountId, @ModelAttribute Account account) {
//	    userService.saveAccount(account);
//	    System.out.println("Account saved successfully for account ID: " + accountId);
//	    return "redirect:/users/" + userId; 
//	}

	@PostMapping("/users/{userId}/accounts/{accountId}/save")
	public String saveAccount(@PathVariable Long userId, @PathVariable Long accountId, @ModelAttribute Account account) {
		account.setAccountId(accountId); 
	    userService.saveAccount(account); 
	    return "redirect:/users/" + userId; 
	}

	
	
//	@PostMapping("/users/{userId}/accounts/{accountId}/save")
//	public String saveAccount(@PathVariable Long userId, @PathVariable Long accountId, @ModelAttribute Account account) {
//	    userService.saveAccount(account);
//	    System.out.println("Account saved successfully for account ID: " + accountId);
//	    return "redirect:/users/" + userId;
//	}
	
	@PostMapping("/users/save")
	public String saveUser(@ModelAttribute User user) {
	    userService.saveUser(user);
	    System.out.println("User saved successfully for user ID: " + user.getUserId());
	    return "redirect:/users";
	}


//	@PostMapping("/users/save")
//	public String saveUser(@PathVariable Long userId, @PathVariable Long accountId, @ModelAttribute Account account) {
//		User user = new User();
//		user.setUserId(userId);
//		userService.saveAccount(account);
//		System.out.println("Account saved successfully for user ID: " + userId);
//		return "redirect:/users";
//	}

	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String showAccountForm(@PathVariable Long userId, @PathVariable Long accountId, Model model) {
		System.out.println("showAccountForm");
		User user = userService.findById(userId);
		System.out.println("User fetched: " + user);
	    Account account = userService.findByAccountId(accountId);
	    
	    model.addAttribute("user", user);
	    model.addAttribute("account", account);

	    System.out.println("Account Id: "+ accountId);
	    System.out.println("User Id: "+ userId);
	    return "account"; 
	}
	
	@GetMapping("/users/{userId}/accounts/{accountId}/info")
	public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
	    Account account = userService.findByAccountId(accountId);
	    if (account == null) {
	        return "redirect:/users/" + userId + "/accounts/new"; 
	    }
	    model.addAttribute("account", account);
	    model.addAttribute("userId", userId);
	    return "account";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postOneAccount(@PathVariable Long userId, @PathVariable(required = false) Long accountId, @ModelAttribute Account account) {
	    if (accountId == null || account.getAccountId() == null) {
	       
	        Account newAccount = userService.createAccountForUser(userId);
	        newAccount.setAccountName(account.getAccountName());
	        userService.saveAccount(newAccount);
	        System.out.println("New account created successfully for user ID: " + userId);
	        return "redirect:/users/" + userId + "/accounts/" + newAccount.getAccountId();
	    } else {
	        
	        Account existingAccount = userService.findByAccountId(accountId);
	        if (existingAccount != null) {
	            existingAccount.setAccountName(account.getAccountName());
	            userService.saveAccount(existingAccount);
	            System.out.println("Account updated successfully for account ID: " + accountId);
	        }
	        return "redirect:/users/" + userId + "/accounts/" + accountId;
	    }
	}


}
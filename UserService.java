package com.coderscampus.assignment13.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.AccountRepository;
import com.coderscampus.assignment13.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AccountRepository accountRepo;
	
	public List<User> findByUsername(String username) {
		return userRepo.findByUsername(username);
	}
	
	public List<Account> findByAccountName(String accountName){
		return accountRepo.findByAccountName(accountName);
	}
	
	public List<User> findByNameAndUsername(String name, String username) {
		return userRepo.findByNameAndUsername(name, username);
	}
	
	public List<User> findByCreatedDateBetween(LocalDate date1, LocalDate date2) {
		return userRepo.findByCreatedDateBetween(date1, date2);
	}
	
	public User findExactlyOneUserByUsername(String username) {
		List<User> users = userRepo.findExactlyOneUserByUsername(username);
		if (users.size() > 0)
			return users.get(0);
		else
			return new User();
	}
	
	public Set<User> findAll () {
		return userRepo.findAllUsersWithAccountsAndAddresses();
	}
	
	public User findById(Long userId) {
		Optional<User> userOpt = userRepo.findById(userId);
		User user = userOpt.orElse(new User());
		
		if(user.getAddress() == null) {
			Address newAddress = new Address();
			newAddress.setUser(user);
			user.setAddress(newAddress);
		}
		return user;
	}
	
	public Account findByAccountId(Long accountId) {
	    Optional<Account> accountOpt = accountRepo.findById(accountId);
	    Account account = accountOpt.orElse(new Account()); 
	    return account;
	}

	   public Account createAccountForUser(Long userId) {
	       
	        Optional<User> optionalUser = userRepo.findById(userId);

	        if (optionalUser.isPresent()) {
	            User user = optionalUser.get();
	            
	           
	            Account newAccount = new Account();
	            newAccount.setAccountName("New Account"); 
	            
	            List<User> users = new ArrayList<>();
	            users.add(user);
	            newAccount.setUsers(users);
	            
	            
	            accountRepo.save(newAccount);
	            
	            return newAccount;
	        } else {
	            throw new RuntimeException("User not found with ID: " + userId);
	        }
	    }
	public User saveUser(User user) {
		if (user.getUserId() == null) {
			Account checking = new Account();
			checking.setAccountName("Checking Account");
			checking.getUsers().add(user);
			
			Account savings = new Account();
			savings.setAccountName("Savings Account");
			savings.getUsers().add(user);
			
			user.getAccounts().add(checking);
			user.getAccounts().add(savings);
			
			accountRepo.save(checking);
			accountRepo.save(savings);
		}
		return userRepo.save(user);
	}

	public void delete(Long userId) {
		userRepo.deleteById(userId);
	}
	
	public Account saveAccount(Account account) {
		return accountRepo.save(account);
	}
	
	public void saveAccount(Long userId, Account account) {
		User user= userRepo.findById(userId)
								 .orElseThrow(() -> new RuntimeException("User not found"));
		
		List<User> users = new ArrayList<>();
		users.add(user);
		account.setUsers(users);
		
		accountRepo.save(account);
	}

	public Account updateAccount(Long userId, Long accountId, Account account) {
		return accountRepo.save(account);
		
	}
}

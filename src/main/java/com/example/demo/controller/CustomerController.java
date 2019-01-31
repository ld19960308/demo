package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;

@Controller
@RequestMapping(path="/demo")
public class CustomerController {
	@Autowired
	private CustomerRepository customerRepository;
	
	@GetMapping(path="/add")
	public @ResponseBody String addNewCustomer(@RequestParam String firstName,
			                                    @RequestParam String lastName){
		Customer c=new Customer(firstName,lastName);
		this.customerRepository.save(c);
		return "saved";
	}
	@GetMapping(path="/all")
	public @ResponseBody Iterable<Customer> getCustomer(){
		return this.customerRepository.findAll();
	}
	@GetMapping(path="/del")
	public @ResponseBody String delCustomer(@RequestParam Integer id){
		this.customerRepository.deleteById(id);
		return "deleted";
	}
	@GetMapping(path="/update")
	public @ResponseBody String updateCustomer(@RequestParam Integer id,@RequestParam String firstName,@RequestParam String lastName){
		//Customer c=this.customerRepository.findById(id);
		Customer c=new Customer(id,firstName,lastName);
		this.customerRepository.save(c);
		return "updated";
	}
	

}

package org.but.feec.bds.service;

import org.but.feec.bds.api.SignInView;
import org.but.feec.bds.data.CustomerRepository;
import org.but.feec.bds.exceptions.ResourceNotFoundException;

import static org.but.feec.bds.service.Argon2Service.ARGON2;
public class AuthService {
    private CustomerRepository customerRepository;

    public AuthService(CustomerRepository customerRepository){this.customerRepository = customerRepository; }

    private SignInView findCustomerByUsername(String username){return customerRepository.findCustomerByUsername(username); }

    public boolean authenticate(String username, String password){
        if(username == null || username.isEmpty() || password == null || password.isEmpty()){
            return false;
        }
        SignInView signInView = findCustomerByUsername(username);
        if(signInView == null){
            throw new ResourceNotFoundException("Provided username is not found.");
        }
        return ARGON2.verify(signInView.getPassword(), password.toCharArray());
    }

}

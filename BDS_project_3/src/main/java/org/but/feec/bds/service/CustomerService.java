package org.but.feec.bds.service;

import org.but.feec.bds.api.CustomerCreateView;
import org.but.feec.bds.data.CustomerRepository;

import static org.but.feec.bds.service.Argon2Service.ARGON2;

public class CustomerService {
    private CustomerRepository customerRepository;

    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CustomerService.username = username;
    }

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void createCustomer(CustomerCreateView customerCreateView){

        char[] originalPassword = customerCreateView.getPassword();
        char[] hashedPassword = hashPassword(originalPassword);
        customerCreateView.setPassword(hashedPassword);

        customerRepository.createCustomer(customerCreateView);
    }
    public char[] hashPassword(char[] password) {
        return ARGON2.hash(10, 65536, 1, password).toCharArray();
    }
}

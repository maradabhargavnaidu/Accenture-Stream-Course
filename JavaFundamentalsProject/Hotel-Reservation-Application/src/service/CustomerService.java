package service;

import model.Customer;

import java.util.Collection;
import java.util.HashMap;

public class CustomerService {
    HashMap<String,Customer> customers = new HashMap<>();

    private static final CustomerService instance = new CustomerService();
    public static CustomerService getInstance(){
        return instance;
    }
    public void addCustomer(String firstName,String lastName,String email){
        customers.put(email,new Customer(firstName,lastName,email));
    }
    public Customer getCustomer(String customerMail){
        return customers.get(customerMail);
    }
    public Collection<Customer> getAllCustomers(){
        return customers.values();
    }

}

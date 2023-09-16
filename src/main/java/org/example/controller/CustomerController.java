package org.example.controller;

import org.example.entity.Customer;
import org.example.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private EndpointService endpointService;

    @GetMapping("/getCustomer")
    public Object getCustomer(@RequestParam(value="id", required = true) String id){

        try {
            return endpointService.getCustomerDetails(Integer.parseInt(id));
        }catch(Exception e){
            System.out.println("An error has occurred " + e.getMessage());
            return new ResponseEntity<Optional<Customer>>(Optional.ofNullable(null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllCustomers")
    public Object geAllCustomers(){
        try {
            return endpointService.getAllCustomers();
        }catch(Exception e){
            System.out.println("An error has occurred" + e.getMessage());
            String response = "An error has occurred: " + e.getMessage() + System.lineSeparator() + "Customer could not be retrieved.";
            return new ResponseEntity(response, HttpStatus.NOT_ACCEPTABLE);        }
    }

    @GetMapping("/test")
    public String test(){
        return new String("test successful");
    }
}

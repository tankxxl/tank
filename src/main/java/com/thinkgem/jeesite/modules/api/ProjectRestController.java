package com.thinkgem.jeesite.modules.api;

import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Created by rgz on 28/04/2017.
 */
// @RestController
// @RequestMapping(value = "${apiPath}/hello")
public class ProjectRestController {

    @Autowired
    ProjectApplyExternalService externalService;

    @Autowired
    CustomerService customerService;

    // Retrieve All Client
    @RequestMapping(value = "/customer/", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> listAllCustomers() {
        List<Customer>  customers = customerService.findList(new Customer());

        if (customers.isEmpty()) {
            // You may decide to return HttpStatus.NOT_FOUND
            return new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }

    // Retrieve Single Client
    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") long id) {
        System.out.println("Fetching Client with id " + id);
        Customer customer = customerService.get(String.valueOf(id));
        if (customer == null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    // Create a Client
    @RequestMapping(value = "/customer/", method = RequestMethod.POST)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Client " + customer.getCustomerName());

        if (customerService.get(customer.getId()) != null) {
            System.out.println("A Client with name " + customer.getCustomerName() + " already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        customerService.save(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customer/{id}").buildAndExpand(customer.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Update a Client
    @RequestMapping(value = "/customer/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
        System.out.println("Updating Client " + id);

        Customer currentCustomer = customerService.get(String.valueOf(id));

        if (currentCustomer == null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        currentCustomer.setCustomerName(customer.getCustomerName());

        customerService.save(currentCustomer);
        return new ResponseEntity<Customer>(currentCustomer, HttpStatus.OK);
    }

    // Delete a Client

}

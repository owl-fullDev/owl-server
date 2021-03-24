package com.owl.owlserver.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomerService {

    //injecting repositories for database access
    @Autowired
    SaleDetailRepository saleDetailRepository;
    @Autowired
    SaleRepository saleRepository;
    @Autowired
    CustomerRepository customerRepository;


    @Transactional
    public void newCustomer() throws JsonProcessingException {

    }

    @Transactional
    public void updateCustomer() throws JsonProcessingException {

    }
}

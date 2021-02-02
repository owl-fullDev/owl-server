package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findAllByFirstNameAndLastName(String firstName, String lastName);
    List<Customer> findAllByPhoneNumber(String phoneNumber);
}

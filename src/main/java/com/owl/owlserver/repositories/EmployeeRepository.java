package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository <Employee,Integer> {

    boolean existsDistinctByFirstName(String firstName);
    boolean existsDistinctByLastname(String lastName);

}
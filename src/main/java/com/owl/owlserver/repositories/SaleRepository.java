package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;

@Repository
public interface SaleRepository extends JpaRepository <Sale,Integer> {
}



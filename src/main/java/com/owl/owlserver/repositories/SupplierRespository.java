package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRespository extends JpaRepository<Supplier,Integer> {



}

package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository <Sale,Integer> {

    List<Sale> getAllByStoreIdEqualsAndAndPickupDateEquals(int storeId, LocalDateTime localDateTime);
}



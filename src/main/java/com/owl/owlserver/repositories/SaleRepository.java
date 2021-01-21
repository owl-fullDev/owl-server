package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository <Sale,Integer> {

    List<Sale> getAllByStoreStoreIdAndPickupDateEquals(int storeId, LocalDateTime localDateTime);
    List<Sale> getAllByInitialDepositDateIsBetweenOrderByInitialDepositDate(LocalDateTime start, LocalDateTime end);

}



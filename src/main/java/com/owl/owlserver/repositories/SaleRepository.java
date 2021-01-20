package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository <Sale,Integer> {

    @Query(value = "SELECT SUM(grand_total) FROM OWL_Database.sale where store_id = 1 GROUP BY store_id", nativeQuery = true)
    double returnedArray();

    List<Sale> getAllByStoreStoreIdAndPickupDateEquals(int storeId, LocalDateTime localDateTime);
    List<Sale> getAllByInitialDepositDateIsBetweenOrderByInitialDepositDate(LocalDateTime start, LocalDateTime end);

}



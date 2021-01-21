package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StoreRepository extends JpaRepository <Store,Integer> {

    Boolean existsByLocation(String location);

    @Query(value = "SELECT SUM(grand_total) FROM OWL_Database.sale where store_id = :storeId and initial_deposit_date between :startDate and :endDate ", nativeQuery = true)
    double totalStoreRevenue(int storeId, String startDate, String endDate);

}

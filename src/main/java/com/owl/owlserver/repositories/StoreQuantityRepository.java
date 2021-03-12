package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Store;
import com.owl.owlserver.model.StoreQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreQuantityRepository extends JpaRepository <StoreQuantity,Integer> {

    StoreQuantity findByStoreAndProductId(Store store, String productId);
}

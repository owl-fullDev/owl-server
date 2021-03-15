package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Store;
import com.owl.owlserver.model.StoreQuantity;
import com.owl.owlserver.model.WarehouseQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreQuantityRepository extends JpaRepository <StoreQuantity,Integer> {

    StoreQuantity findByStoreAndProduct_ProductId(Store store, String productId);
    StoreQuantity findByStore_StoreIdAndProduct_ProductId(int storeId, String productId);
    List<StoreQuantity> findAllByStore_StoreId(int storeId);

}

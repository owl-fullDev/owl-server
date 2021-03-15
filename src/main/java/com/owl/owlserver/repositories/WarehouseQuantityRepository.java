package com.owl.owlserver.repositories;

import com.owl.owlserver.model.WarehouseQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseQuantityRepository extends JpaRepository<WarehouseQuantity,Integer> {

    WarehouseQuantity findByProduct_ProductId(String productId);
    WarehouseQuantity findByWarehouseWarehouseIdAndProduct_ProductId(int warehouseId, String productId);
    List<WarehouseQuantity> findAllByProduct_ProductIdIn(List<String> productIdList);

}
package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Warehouse;
import com.owl.owlserver.model.WarehouseQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseQuantityRepository extends JpaRepository<WarehouseQuantity,Integer> {

    WarehouseQuantity findByProductId (String productId);
    WarehouseQuantity findByWarehouseWarehouseIdAndProductId(int warehouseId, String productId);

}
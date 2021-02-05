package com.owl.owlserver.repositories;

import com.owl.owlserver.model.ShipmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentDetailRepository extends JpaRepository<ShipmentDetail,Integer> {

}

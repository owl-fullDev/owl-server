package com.owl.owlserver.repositories;

import com.owl.owlserver.model.RestockShipment;
import com.owl.owlserver.model.RestockShipmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestockShipmentDetailRepository extends JpaRepository<RestockShipmentDetail,Integer> {

}

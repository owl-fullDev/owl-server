package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Integer> {

    List<Shipment> findAllByReceivedTimestampIsNull();
    List<Shipment> findAllByReceivedTimestampIsNullAndDestinationTypeEquals(int destinationType);

}

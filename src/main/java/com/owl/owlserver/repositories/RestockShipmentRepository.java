package com.owl.owlserver.repositories;

import com.owl.owlserver.model.RestockShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestockShipmentRepository extends JpaRepository<RestockShipment,Integer> {

    List<RestockShipment> findAllByReceivedTimestampIsNull();
}

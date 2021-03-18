package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund,Integer> {

    List<Refund> getAllByRefundDateIsBetween(LocalDateTime start, LocalDateTime end);

}

package com.owl.owlserver.repositories;

import com.owl.owlserver.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleDetailRepository extends JpaRepository <SaleDetail,Integer> {
}

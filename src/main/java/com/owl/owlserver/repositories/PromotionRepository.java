package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository <Promotion,Integer> {

    boolean existsDistinctByPercentage(int percentage);
    boolean existsDistinctByPromotionName(String name);

}
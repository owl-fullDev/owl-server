package com.owl.owlserver.repositories.Products;

import com.owl.owlserver.model.Products.LensCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LensCategoryRepository extends JpaRepository<LensCategory,Integer> {

    LensCategory findTopByOrderByLensCategoryIdDesc();
}

package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository <Product, String> {

    @Query("SELECT productId FROM Product WHERE productId IN ?1")
    List<String> findProductIdByProductIdIn(List<String> productIdList);
    List<Product> findAllByProductIdStartsWith(String startingSequence);
}


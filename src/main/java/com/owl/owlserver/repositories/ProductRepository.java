package com.owl.owlserver.repositories;

import com.owl.owlserver.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository <Product, String> {

    List<Product> findAllByProductIdStartsWith(String startingChars);

    //frames


    //lenses


    //custom lenses


    //others

}


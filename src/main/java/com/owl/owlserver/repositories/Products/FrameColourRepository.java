package com.owl.owlserver.repositories.Products;

import com.owl.owlserver.model.Products.FrameColour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameColourRepository extends JpaRepository<FrameColour,Integer> {

    FrameColour findTopByOrderByFrameColourId();
}

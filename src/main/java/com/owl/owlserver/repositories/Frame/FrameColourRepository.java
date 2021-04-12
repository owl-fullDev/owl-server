package com.owl.owlserver.repositories.Frame;

import com.owl.owlserver.model.Frame.FrameCategory;
import com.owl.owlserver.model.Frame.FrameColour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameColourRepository extends JpaRepository<FrameColour,Character> {

    FrameColour findTopByOrderByFrameColourId();
}

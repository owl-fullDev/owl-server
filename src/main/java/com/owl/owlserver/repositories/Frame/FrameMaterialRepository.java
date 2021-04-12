package com.owl.owlserver.repositories.Frame;

import com.owl.owlserver.model.Frame.FrameCategory;
import com.owl.owlserver.model.Frame.FrameMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameMaterialRepository extends JpaRepository<FrameMaterial,Character> {
}

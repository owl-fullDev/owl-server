package com.owl.owlserver.repositories.Frame;

import com.owl.owlserver.model.Frame.FrameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameCategoryRepository extends JpaRepository<FrameCategory,Character> {
}

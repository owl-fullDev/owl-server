package com.owl.owlserver.repositories.Frame;

import com.owl.owlserver.model.Frame.FrameCategory;
import com.owl.owlserver.model.Frame.FrameModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameModelRepository extends JpaRepository<FrameModel,Character> {

    boolean existsByFrameCategoryAndAndFrameModelCode(FrameCategory frameCategoryId, String frameModelCode);
}

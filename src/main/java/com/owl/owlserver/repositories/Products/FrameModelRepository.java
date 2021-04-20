package com.owl.owlserver.repositories.Products;

import com.owl.owlserver.model.Products.FrameModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameModelRepository extends JpaRepository<FrameModel,Integer> {

    FrameModel findAllByFrameCategory_FrameCategoryIdAndFrameCategoryModelId(int frameCategoryId, int frameCategoryModelId);

}

package com.owl.owlserver.repositories.Products;

import com.owl.owlserver.model.Products.LensModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LensModelRepository extends JpaRepository<LensModel,Integer> {

    LensModel findByLensCategory_LensCategoryIdAndLensCategoryModelId(int lensCategoryId, int lensCategoryModelId);
}

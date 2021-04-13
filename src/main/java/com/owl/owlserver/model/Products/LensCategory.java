package com.owl.owlserver.model.Products;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "LENS_CATEGORY")
public class LensCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LENS_CATEGORY_ID", nullable = false)
    private int lensCategoryId;

    @Column(name = "CATEGORY_NAME", nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "lensCategory")
    private List<LensModel> lensModelList;

    public LensCategory() {
        lensModelList = new ArrayList<>();
    }

    public LensCategory(String categoryName) {
        lensModelList = new ArrayList<>();
        this.categoryName = categoryName;
    }

    public void addModel(LensModel lensModel) {
        lensModelList.add(lensModel);
    }

}
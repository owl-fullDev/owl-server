package com.owl.owlserver.model.Products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@Table(name = "LENS_MODEL")
public class LensModel implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LENS_MODEL_ID", nullable = false)
    private int lensModelId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "LENS_CATEGORY_ID", nullable = false)
    private LensCategory lensCategory;

    @Column(name = "lens_category_model_id" ,nullable = false)
    private int lensCategoryModelId;

    @Column(name = "LENS_MODEL", nullable = false)
    private String lensModel;

    public LensModel(LensCategory lensCategory, int lensCategoryModelId, String lensModel) {
        this.lensCategory = lensCategory;
        this.lensCategoryModelId = lensCategoryModelId;
        this.lensModel = lensModel;
    }
}
package com.owl.owlserver.model.Products;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "FRAME_CATEGORY")
public class FrameCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRAME_CATEGORY_ID", nullable = false)
    private int frameCategoryId;

    @Column(name = "CATEGORY_NAME", nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "frameCategory")
    private List<FrameModel> frameModelList;

    public FrameCategory() {
        frameModelList = new ArrayList<>();
    }

    public FrameCategory(String categoryName) {
        frameModelList = new ArrayList<>();
        this.categoryName = categoryName;
    }

    public void addModel(FrameModel frameModel) {
        frameModelList.add(frameModel);
    }

}
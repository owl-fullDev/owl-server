package com.owl.owlserver.model.Frame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@Table(name = "FRAME_MATERIAL")
public class FrameMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRAME_MATERIAL_ID", nullable = false)
    private int frameMaterialId;

    @Column(name = "FRAME_MATERIAL", nullable = false)
    private String frameMaterial;

    public FrameMaterial(String frameMaterial) {
        this.frameMaterial = frameMaterial;
    }
}
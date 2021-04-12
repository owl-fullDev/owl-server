package com.owl.owlserver.model.Frame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRAME_MATERIAL")
public class FrameMaterial implements Serializable {

    @Id
    @Column(name = "FRAME_MATERIAL_ID", nullable = false)
    private Character frameMaterialId;

    @Column(name = "FRAME_MATERIAL", nullable = false)
    private String frameMaterial;

}
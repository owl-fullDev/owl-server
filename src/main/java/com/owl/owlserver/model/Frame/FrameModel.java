package com.owl.owlserver.model.Frame;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRAME_MODEL")
public class FrameModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRAME_MODEL_ID", nullable = false)
    private int frameModelId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "FRAME_CATEGORY_ID", nullable = false)
    private FrameCategory frameCategory;

    @Column(name = "FRAME_MODEL_CODE", columnDefinition = "char(2)" ,nullable = false)
    private String frameModelCode;

    @Column(name = "FRAME_MODEL", nullable = false)
    private String frameModel;

    public FrameModel(FrameCategory frameCategory, String frameModelCode, String frameModel) {
        this.frameCategory = frameCategory;
        this.frameModelCode = frameModelCode;
        this.frameModel = frameModel;
    }
}
package com.owl.owlserver.model.Frame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@Table(name = "FRAME_COLOUR")
public class FrameColour implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRAME_COLOUR_ID", nullable = false)
    private int frameColourId;

    @Column(name = "FRAME_COLOUR", nullable = false)
    private String frameColour;

    public FrameColour(String frameColour) {
        this.frameColour = frameColour;
    }
}



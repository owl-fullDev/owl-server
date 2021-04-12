package com.owl.owlserver.model.Frame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRAME_COLOUR")
public class FrameColour implements Serializable {

    @Id
    @Column(name = "FRAME_COLOUR_ID", nullable = false)
    private Character frameColourId;

    @Column(name = "FRAME_COLOUR", nullable = false)
    private String frameColour;

}
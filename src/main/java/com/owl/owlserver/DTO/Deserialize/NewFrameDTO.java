package com.owl.owlserver.DTO.Deserialize;

import lombok.Data;

@Data
public class NewFrameDTO {

    public int frameCategoryId;
    public int frameModelId;
    public int frameMaterial;

    public int[] frameColourIdArray;
}
